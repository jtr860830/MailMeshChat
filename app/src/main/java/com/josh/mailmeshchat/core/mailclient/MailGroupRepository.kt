package com.josh.mailmeshchat.core.mailclient

import com.josh.mailmeshchat.core.data.model.Group
import com.josh.mailmeshchat.core.data.model.mapper.toGroupSerializable
import com.josh.mailmeshchat.core.mailclient.JavaMailClient.Companion.FOLDER_GROUPS
import com.josh.mailmeshchat.core.mailclient.JavaMailClient.Companion.FOLDER_MESSAGES
import com.josh.mailmeshchat.core.mailclient.JavaMailClient.Companion.HEADER_GROUP
import com.josh.mailmeshchat.core.mailclient.JavaMailClient.Companion.HEADER_ID
import com.josh.mailmeshchat.core.mailclient.JavaMailClient.Companion.HEADER_TIMESTAMP
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID
import javax.mail.Folder
import javax.mail.Message
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

fun JavaMailClient.createGroup(to: Array<String>, name: String? = "") {
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

fun JavaMailClient.fetchGroups(): Flow<List<Message>> {
    return flow {
        val folder = getFolder(FOLDER_GROUPS)
        folder.open(Folder.READ_ONLY)

        emit(folder.messages.toList())
        folder.close(false)
    }
}