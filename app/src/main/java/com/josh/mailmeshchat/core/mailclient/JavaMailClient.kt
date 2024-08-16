package com.josh.mailmeshchat.core.mailclient

import com.josh.mailmeshchat.core.data.model.Contact
import com.josh.mailmeshchat.core.data.model.Group
import com.josh.mailmeshchat.core.data.model.UserInfo
import com.josh.mailmeshchat.core.data.model.mapper.toContact
import com.josh.mailmeshchat.core.data.model.mapper.toContactSerializable
import com.josh.mailmeshchat.core.data.model.mapper.toGroupSerializable
import com.josh.mailmeshchat.core.data.model.serializable.ContactSerializable
import com.josh.mailmeshchat.core.sharedpreference.UserInfoStorage
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
abstract class JavaMailClient(private val userStorage: UserInfoStorage) {

    private var smtpSession: Session? = null
    private var userInfo: UserInfo? = null
    private var store: Store? = null

    protected abstract fun configureSMTP(email: String?, password: String?): Session
    protected abstract fun configureIMAP(): Store

    fun createGroup(to: Array<String>, name: String? = "") {
        val recipients = to.map { InternetAddress(it) }.toTypedArray()
        smtpSession?.let {
            val uuid = UUID.randomUUID().toString()
            val group = Group(uuid, name!!, to.toList())
            val message = Json.encodeToString(group.toGroupSerializable())

            val groupMessage = MimeMessage(smtpSession).apply {
                setHeader(HEADER_TIMESTAMP, System.currentTimeMillis().toString())
                setHeader(HEADER_GROUP, uuid)
                setFrom(InternetAddress(userInfo?.email))
                setRecipients(Message.RecipientType.TO, recipients)
                subject = uuid
                setText(message)
            }
            Transport.send(groupMessage)
            appendMessage(FOLDER_GROUPS, groupMessage)

            val firstMessage = MimeMessage(smtpSession).apply {
                setHeader(HEADER_ID, uuid)
                setHeader(HEADER_TIMESTAMP, System.currentTimeMillis().toString())
                setFrom(InternetAddress(userInfo?.email))
                setRecipients(Message.RecipientType.TO, recipients)
                subject = uuid
                setText("This is the first message of the group, send by system.")
            }
            Transport.send(firstMessage)
            appendMessage(FOLDER_MESSAGES, firstMessage)
        }
    }

    private fun appendMessage(folderName: String, message: Message) {
        val folder = getFolder(folderName)
        folder.open(Folder.READ_WRITE)
        folder.appendMessages(arrayOf(message))
        folder.close(false)
    }

    private fun moveMessageToFolder() {
        val folder = store!!.getFolder(FOLDER_INBOX)
        folder.open(Folder.READ_WRITE)

        val messages = folder?.messages
        messages?.let {
            for (message in messages) {
                val isMessage = !message.getHeader(HEADER_ID).isNullOrEmpty()
                if (isMessage) appendMessage(FOLDER_MESSAGES, message)

                val isGroup = !message.getHeader(HEADER_GROUP).isNullOrEmpty()
                if (isGroup) appendMessage(FOLDER_GROUPS, message)
            }
        }

        folder.addMessageCountListener(object : MessageCountListener {
            override fun messagesAdded(e: MessageCountEvent?) {
                e?.messages?.toList()?.let {
                    for (message in it) {
                        val isMessage = !message.getHeader(HEADER_ID).isNullOrEmpty()
                        if (isMessage) appendMessage(FOLDER_MESSAGES, message)

                        val isGroup = !message.getHeader(HEADER_GROUP).isNullOrEmpty()
                        if (isGroup) appendMessage(FOLDER_GROUPS, message)
                    }
                }
            }

            override fun messagesRemoved(e: MessageCountEvent?) {

            }
        })

        while (true) {
            (folder as IMAPFolder).idle()
        }
    }

    fun fetchGroups(): Flow<List<Message>> {
        return flow {
            val folder = getFolder(FOLDER_GROUPS)
            folder.open(Folder.READ_ONLY)

            emit(folder.messages.toList())
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
                            if (hasMMCId && message.subject.removeAllPrefixes("Re: ") == subject) {
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
                appendMessage(FOLDER_MESSAGES, message)
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
    }

    fun addContact(contact: Contact) {
        val folder = getFolder(FOLDER_CONTACTS)
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

    private fun getFolder(folderName: String): Folder {
        if (!store!!.isConnected) {
            store!!.connect(userInfo?.email, userInfo?.password)
        }
        val folder = store!!.getFolder(folderName)
        if (!folder.exists()) {
            folder.create(Folder.HOLDS_MESSAGES)
        }
        return folder
    }

    fun observeFolder(folderName: String): Flow<Unit> {
        return callbackFlow {
            val folder = getFolder(folderName)
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

    companion object {
        const val FOLDER_INBOX = "INBOX"
        const val FOLDER_CONTACTS = "mmc/contacts"
        const val FOLDER_MESSAGES = "mmc/messages"
        const val FOLDER_GROUPS = "mmc/groups"

        const val HEADER_ID = "X-MMC-Id"
        const val HEADER_TIMESTAMP = "X-MMC-Timestamp"
        const val HEADER_GROUP = "X-MMC-Group"
    }
}