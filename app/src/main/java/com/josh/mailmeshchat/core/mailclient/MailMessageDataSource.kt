package com.josh.mailmeshchat.core.mailclient

import com.josh.mailmeshchat.core.mailclient.JavaMailClient.Companion.FOLDER_INBOX
import com.josh.mailmeshchat.core.mailclient.JavaMailClient.Companion.FOLDER_MESSAGES
import com.josh.mailmeshchat.core.mailclient.JavaMailClient.Companion.HEADER_ID
import com.josh.mailmeshchat.core.mailclient.JavaMailClient.Companion.HEADER_TIMESTAMP
import com.josh.mailmeshchat.core.util.removeAllPrefixes
import com.sun.mail.imap.IMAPFolder
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import javax.mail.Flags
import javax.mail.Folder
import javax.mail.Message
import javax.mail.Transport
import javax.mail.event.MessageCountEvent
import javax.mail.event.MessageCountListener
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import javax.mail.search.SubjectTerm

fun JavaMailClient.fetchMessagesBySubject(subject: String): Flow<List<Message>> {
    return flow {
        val folder = getFolder(FOLDER_MESSAGES)
        folder.open(Folder.READ_WRITE)

        val messages = folder.search(SubjectTerm(subject))
        messages.forEach {
            it.setFlag(Flags.Flag.SEEN, true)
        }
        emit(messages.toList())
        folder.close(false)
    }
}

fun JavaMailClient.observeMessagesBySubject(subject: String): Flow<List<Message>> {
    return callbackFlow {
        val folder = store!!.getFolder(FOLDER_INBOX)
        folder.open(Folder.READ_WRITE)

        folder.addMessageCountListener(object : MessageCountListener {
            override fun messagesAdded(e: MessageCountEvent?) {
                e?.messages?.toList()?.let {
                    for (message in it) {
                        val hasMMCId = message.getHeader(HEADER_ID)?.isNotEmpty() == true
                        if (hasMMCId && message.subject.removeAllPrefixes("Re: ") == subject) {
                            message.setFlag(Flags.Flag.SEEN, true)
                            trySend(it)
                        }
                    }
                }
            }

            override fun messagesRemoved(e: MessageCountEvent?) {

            }
        })

        var idle = true
        while (idle) {
            if (!folder.isOpen) break
            (folder as IMAPFolder).idle()
        }

        awaitClose {
            idle = false
            folder.close(false)
        }
    }
}

fun JavaMailClient.replyMessage(
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
        }
    }
}

fun JavaMailClient.fetchUnreadMessageCount(subject: String): Flow<Int> {
    return flow {
        val folder = getFolder(FOLDER_MESSAGES)
        folder.open(Folder.READ_ONLY)
        val messages = folder.search(SubjectTerm(subject))
        val unreadMessageCount = messages.count { !it.isSet(Flags.Flag.SEEN) }
        emit(unreadMessageCount)
        folder.close(false)
    }
}