package com.josh.mailmeshchat.core.mailclient

import com.josh.mailmeshchat.core.data.model.Contact
import com.josh.mailmeshchat.core.data.model.ContactSerializable
import com.josh.mailmeshchat.core.data.model.UserInfo
import com.josh.mailmeshchat.core.data.model.mapper.toContact
import com.josh.mailmeshchat.core.data.model.mapper.toContactSerializable
import com.josh.mailmeshchat.core.sharedpreference.UserStorage
import com.josh.mailmeshchat.core.util.removeAllPrefixes
import com.sun.mail.imap.IMAPFolder
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID
import javax.mail.Flags
import javax.mail.Folder
import javax.mail.Message
import javax.mail.Session
import javax.mail.Store
import javax.mail.Transport
import javax.mail.event.MessageCountEvent
import javax.mail.event.MessageCountListener
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import javax.mail.search.SubjectTerm


// todo: decoupling userStorage from JavaMailClient
abstract class JavaMailClient(private val userStorage: UserStorage) {

    private var smtpSession: Session? = null
    private var userInfo: UserInfo? = null
    private var store: Store? = null
    private var inbox: Folder? = null

    protected abstract fun configureSMTP(email: String?, password: String?): Session
    protected abstract fun configureIMAP(): Store

    suspend fun sendMessage(
        to: Array<String>,
        message: String,
    ) {
        val recipients = to.map { InternetAddress(it) }.toTypedArray()
        smtpSession?.let {
            val uuid = UUID.randomUUID().toString()

            val mail = MimeMessage(smtpSession).apply {
                setHeader(HEADER_ID, uuid)
                setHeader(HEADER_TIMESTAMP, System.currentTimeMillis().toString())
                setFrom(InternetAddress(userInfo?.email))
                setRecipients(Message.RecipientType.TO, recipients)
                subject = uuid
                setText(message)
            }

            Transport.send(mail)
            // todo: check is necessary to append message when send to self
            appendMessage(mail)
        }
    }

    private fun appendMessage(message: Message) {
        val folder = getFolder(FOLDER_MESSAGES)
        folder.open(Folder.READ_WRITE)
        folder.appendMessages(arrayOf(message))
        folder.close(false)
    }

    private fun moveMessageToFolder() {
        val sourceFolder = store!!.getFolder(FOLDER_INBOX)
        sourceFolder.open(Folder.READ_WRITE)

        val messages = sourceFolder?.messages
        messages?.let {
            for (message in messages) {
                val hasMMCId = message.getHeader(HEADER_ID)?.isNotEmpty() == true
                if (hasMMCId) appendMessage(message)
            }
        }

        sourceFolder.close(true)
    }

    fun fetchMessages(): Flow<List<Message>> {
        return flow {
            val folder = getFolder(FOLDER_MESSAGES)
            folder.open(Folder.READ_ONLY)

            val messages = folder.messages.filter { !it.subject.startsWith("Re: ") }
            emit(messages.toList())
            folder.close(false)
        }
    }

    fun fetchMessagesBySubject(subject: String): Flow<List<Message>> {
        return flow {
            val folder = getFolder(FOLDER_MESSAGES)
            folder.open(Folder.READ_ONLY)

            val messages = folder.search(SubjectTerm(subject))
            emit(messages.toList())
            folder.close(false)
        }
    }

    fun observeMessagesBySubject(subject: String): Flow<List<Message>> {
        return callbackFlow {
            val folder = store!!.getFolder(FOLDER_INBOX)
            folder.open(Folder.READ_ONLY)

            folder.addMessageCountListener(object : MessageCountListener {
                override fun messagesAdded(e: MessageCountEvent?) {
                    e?.messages?.toList()?.let {
                        for (message in it) {
                            val hasMMCId = message.getHeader(HEADER_ID)?.isNotEmpty() == true
                            if (hasMMCId && message.subject.removeAllPrefixes("Re: ") == subject) trySend(
                                it
                            )
                        }
                    }
                }

                override fun messagesRemoved(e: MessageCountEvent?) {

                }
            })

            var idle = true
            while (idle) {
                (folder as IMAPFolder).idle()
            }

            awaitClose {
                idle = false
                folder.close(false)
            }
        }
    }

    suspend fun replyMessage(
        subject: String,
        replyMessage: String
    ) {
        smtpSession?.let {
            val folder = getFolder(FOLDER_MESSAGES)
            folder.open(Folder.READ_ONLY)

            val messages = folder.search(SubjectTerm(subject))
            messages.sortBy { it.sentDate }

            val rootMessage = messages?.firstOrNull()
            rootMessage?.let { originalMessage ->
                val message = MimeMessage(smtpSession).apply {
                    setHeader(HEADER_ID, originalMessage.subject)
                    setHeader(HEADER_TIMESTAMP, System.currentTimeMillis().toString())
                    setFrom(InternetAddress(userInfo?.email))
                    setRecipients(Message.RecipientType.TO, originalMessage.allRecipients)
                    setSubject("Re: ${originalMessage.subject}")
                    setText(replyMessage)
                }
                Transport.send(message)
                // todo: check is necessary to append message when send to self
                appendMessage(message)
            }
        }
    }

    suspend fun connect() {
        try {
            userInfo = userStorage.get()
            smtpSession = configureSMTP(userInfo?.email, userInfo?.password)

            store = configureIMAP()
            store?.connect(userInfo?.email, userInfo?.password)

            moveMessageToFolder()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun disconnect() {
        smtpSession = null
        userInfo = null
        if (store?.isConnected == true) {
            store?.close()
        }
        if (inbox?.isOpen == true) {
            inbox?.close(false)
        }

    }

    fun addContact(contact: Contact) {
        val folder = getFolder(FOLDER_CONTACTS)
        if (!folder.exists()) {
            folder.create(Folder.HOLDS_MESSAGES)
        }
        folder.open(Folder.READ_WRITE)

        val message = MimeMessage(smtpSession)
        message.subject = UUID.randomUUID().toString()
        message.setText(Json.encodeToString(contact.toContactSerializable()))

        folder.appendMessages(arrayOf(message))

        folder.close(false)
    }

    fun fetchContact(): Flow<List<Contact>> {
        return flow {
            val folder = getFolder(FOLDER_CONTACTS)
            folder.open(Folder.READ_ONLY)

            val messages = folder.messages
            val contacts = mutableListOf<Contact>()

            for (message in messages) {
                val content = message.content.toString()
                val contact = Json.decodeFromString<ContactSerializable>(content)
                contacts.add(contact.toContact(message.subject))
            }

            folder.close(false)
            emit(contacts)
        }
    }

    fun deleteContact(contact: Contact) {
        val folder = getFolder(FOLDER_CONTACTS)
        folder.open(Folder.READ_WRITE)

        val messages = folder.search(SubjectTerm(contact.id))
        for (message in messages) {
            message.setFlag(Flags.Flag.DELETED, true)
        }

        folder.close(true)
    }

    fun observeContact(): Flow<Unit> {
        return callbackFlow {
            val folder = getFolder(FOLDER_CONTACTS)
            folder.open(Folder.READ_ONLY)

            folder.addMessageCountListener(object : MessageCountListener {
                override fun messagesAdded(e: MessageCountEvent?) {
                    trySend(Unit)
                }

                override fun messagesRemoved(e: MessageCountEvent?) {
                    trySend(Unit)
                }
            })

            var idle = true
            while (idle) {
                (folder as IMAPFolder).idle()
            }

            awaitClose {
                folder.close(false)
                idle = false
            }
        }
    }

    private fun getFolder(folderName: String): Folder {
        val folder = store!!.getFolder(folderName)
        if (!folder.exists()) {
            folder.create(Folder.HOLDS_MESSAGES)
        }
        return folder
    }

    companion object {
        const val FOLDER_INBOX = "INBOX"
        const val FOLDER_CONTACTS = "mmc/contacts"
        const val FOLDER_MESSAGES = "mmc/messages"
        const val FOLDER_GROUPS = "mmc/groups"

        const val HEADER_ID = "X-MMC-Id"
        const val HEADER_TIMESTAMP = "X-MMC-Timestamp"
    }
}