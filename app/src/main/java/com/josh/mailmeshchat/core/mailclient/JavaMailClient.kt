package com.josh.mailmeshchat.core.mailclient

import com.josh.mailmeshchat.core.data.model.UserInfo
import com.josh.mailmeshchat.core.sharedpreference.UserStorage
import com.josh.mailmeshchat.core.util.removeAllPrefixes
import com.sun.mail.imap.IMAPFolder
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.UUID
import javax.mail.*
import javax.mail.event.MessageCountEvent
import javax.mail.event.MessageCountListener
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import javax.mail.search.SubjectTerm
import com.josh.mailmeshchat.core.data.model.Message as LocalMessage


// todo: decoupling userStorage from JavaMailClient
abstract class JavaMailClient(private val userStorage: UserStorage) {

    private var smtpSession: Session? = null
    private var userInfo: UserInfo? = null
    private var store: Store? = null
    private var inbox: Folder? = null

    protected abstract fun configureSMTP(email: String?, password: String?): Session
    protected abstract fun configureIMAP(): Store

    suspend fun send(
        to: String,
        message: String,
        onSendSuccess: suspend (message: LocalMessage) -> Unit
    ) {
        smtpSession?.let {
            val uuid = UUID.randomUUID().toString()
            try {
                val mail = MimeMessage(smtpSession).apply {
                    setFrom(InternetAddress(userInfo?.email))
                    setRecipients(Message.RecipientType.TO, InternetAddress.parse(to))
                    subject = uuid
                    setText(message)
                }
                Transport.send(mail)
                onSendSuccess.invoke(
                    LocalMessage(
                        sender = userInfo?.email ?: "",
                        subject = uuid,
                        message = message,
                        timestamp = System.currentTimeMillis()
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun reply(
        subject: String,
        replyMessage: String,
        onSendSuccess: suspend (message: LocalMessage) -> Unit
    ) {
        smtpSession?.let {
            try {
                val messageToReply = inbox?.search(SubjectTerm(subject))?.firstOrNull()
                messageToReply?.let { originalMessage ->
                    val message = MimeMessage(smtpSession).apply {
                        setFrom(InternetAddress(userInfo?.email))
                        setRecipients(Message.RecipientType.TO, originalMessage.from)
                        setSubject("Re: ${originalMessage.subject}")
                        setText(replyMessage)
                        replyTo = originalMessage.replyTo ?: originalMessage.from
                    }
                    Transport.send(message)
                    onSendSuccess.invoke(
                        LocalMessage(
                            sender = userInfo?.email ?: "",
                            subject = originalMessage.subject.removeAllPrefixes("Re: "),
                            message = replyMessage,
                            timestamp = System.currentTimeMillis()
                        )
                    )
                } ?: throw Exception("Original message not found")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun connect(): Flow<LocalMessage> {
        return callbackFlow {
            try {
                userInfo = userStorage.get()
                smtpSession = configureSMTP(userInfo?.email, userInfo?.password)

                store = configureIMAP()
                store?.connect(userInfo?.email, userInfo?.password)

                inbox = store?.getFolder("INBOX")
                inbox?.open(Folder.READ_ONLY)

                val messages = inbox?.messages
                messages?.let {
                    for (message in messages) {
                        trySend(
                            LocalMessage(
                                sender = message.from[0].toString(),
                                subject = message.subject.removeAllPrefixes("Re: "),
                                message = message.content.toString(),
                                timestamp = message.sentDate.time
                            )
                        )
                    }
                }

                inbox?.addMessageCountListener(object : MessageCountListener {
                    override fun messagesAdded(e: MessageCountEvent?) {
                        e?.messages?.forEach { message ->
                            trySend(
                                LocalMessage(
                                    sender = message.from[0].toString(),
                                    subject = message.subject.removeAllPrefixes("Re: "),
                                    message = message.content.toString(),
                                    timestamp = message.sentDate.time
                                )
                            )
                        }
                    }

                    override fun messagesRemoved(e: MessageCountEvent?) {

                    }

                })

                var keepRunning = true
                while (keepRunning) {
                    try {
                        (inbox as IMAPFolder).idle()
                    } catch (e: FolderClosedException) {
                        inbox?.open(Folder.READ_ONLY)
                    } catch (e: Exception) {
                        close(e)
                        keepRunning = false
                    }
                }

                awaitClose {
                    keepRunning = false
                    disconnect()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun disconnect() {
        smtpSession = null
        userInfo = null
        store?.close()
        inbox?.close(false)
    }
}