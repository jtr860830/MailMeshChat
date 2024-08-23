package com.josh.mailmeshchat.core.mailclient

import com.josh.mailmeshchat.core.data.model.Group
import com.josh.mailmeshchat.core.data.model.mapper.toGroup
import com.josh.mailmeshchat.core.data.model.mapper.toGroupSerializable
import com.josh.mailmeshchat.core.mailclient.JavaMailClient.Companion.FOLDER_GROUPS
import com.josh.mailmeshchat.core.mailclient.JavaMailClient.Companion.HEADER_GROUP
import com.josh.mailmeshchat.core.mailclient.JavaMailClient.Companion.HEADER_ID
import com.josh.mailmeshchat.core.mailclient.JavaMailClient.Companion.HEADER_TIMESTAMP
import com.josh.mailmeshchat.core.mailclient.JavaMailClient.Companion.PREFIX_GROUP
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
import javax.mail.search.SubjectTerm

fun JavaMailClient.createGroup(to: Array<String>, name: String? = ""): Flow<String> {
    return flow {
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
                subject = "$PREFIX_GROUP$uuid"
                setText(message)
            }
            Transport.send(groupMessage)

            val firstMessage = MimeMessage(smtpSession).apply {
                setHeader(HEADER_ID, uuid)
                setHeader(HEADER_TIMESTAMP, System.currentTimeMillis().toString())
                setFrom(InternetAddress(userInfo?.email))
                setRecipients(Message.RecipientType.TO, recipients)
                subject = uuid
                setText("This is the first message of the group, send by system.")
            }
            Transport.send(firstMessage)

            emit(uuid)
        }
    }
}

fun JavaMailClient.fetchGroups(userEmail: String): Flow<List<Message>> {
    return flow {
        val folder = getFolder(FOLDER_GROUPS)
        folder.open(Folder.READ_ONLY)

        val uniqueMessages = mutableMapOf<String, Message>()

        // get the latest group detail
        for (message in folder.messages) {
            val subject = message.subject
            val timestamp = message.getHeader(HEADER_TIMESTAMP)?.firstOrNull()?.toLongOrNull()

            if (subject != null && timestamp != null) {
                val existingMessage = uniqueMessages[subject]
                if (existingMessage == null || (existingMessage.getHeader(HEADER_TIMESTAMP)
                        ?.firstOrNull()?.toLongOrNull() ?: 0) < timestamp
                ) {
                    uniqueMessages[subject] = message
                }
            }
        }

        // filter out groups that the user is not a member of
        for ((key, message) in uniqueMessages) {
            val group = message.toGroup()
            if (!group.members.contains(userEmail)) {
                uniqueMessages.remove(key)
            }
        }

        emit(uniqueMessages.values.toList())
        folder.close(false)
    }
}

fun JavaMailClient.updateGroupMembers(uuid: String, members: List<String>) {
    val folder = getFolder(FOLDER_GROUPS)
    folder.open(Folder.READ_WRITE)

    val messages = folder.search(SubjectTerm("$PREFIX_GROUP$uuid"))
    val oldGroup = messages.first()

    val modifiedGroup = oldGroup.toGroup()
    modifiedGroup.members = members

    val recipients = members.map { InternetAddress(it) }.toMutableList()

    for (recipient in oldGroup.allRecipients) {
        if (recipients.contains(recipient)) continue
        recipients.add(recipient as InternetAddress)
    }

    val newGroup = MimeMessage(smtpSession).apply {
        setHeader(HEADER_TIMESTAMP, System.currentTimeMillis().toString())
        setHeader(HEADER_GROUP, uuid)
        setFrom(InternetAddress(userInfo?.email))
        setRecipients(Message.RecipientType.TO, recipients.toTypedArray())
        subject = "$PREFIX_GROUP$uuid"
        setText(Json.encodeToString(modifiedGroup.toGroupSerializable()))
    }
    Transport.send(newGroup)

    folder.close(false)
}