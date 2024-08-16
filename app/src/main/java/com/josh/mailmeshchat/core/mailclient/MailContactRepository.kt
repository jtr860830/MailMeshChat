package com.josh.mailmeshchat.core.mailclient

import com.josh.mailmeshchat.core.data.model.Contact
import com.josh.mailmeshchat.core.data.model.mapper.toContact
import com.josh.mailmeshchat.core.data.model.mapper.toContactSerializable
import com.josh.mailmeshchat.core.data.model.serializable.ContactSerializable
import com.josh.mailmeshchat.core.mailclient.JavaMailClient.Companion.FOLDER_CONTACTS
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID
import javax.mail.Flags
import javax.mail.Folder
import javax.mail.internet.MimeMessage
import javax.mail.search.SubjectTerm

fun JavaMailClient.createContact(contact: Contact) {
    val folder = getFolder(FOLDER_CONTACTS)
    folder.open(Folder.READ_WRITE)

    val message = MimeMessage(smtpSession)
    message.subject = UUID.randomUUID().toString()
    message.setText(Json.encodeToString(contact.toContactSerializable()))

    folder.appendMessages(arrayOf(message))

    folder.close(false)
}

fun JavaMailClient.fetchContact(): Flow<List<Contact>> {
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

fun JavaMailClient.deleteContact(contact: Contact) {
    val folder = getFolder(FOLDER_CONTACTS)
    folder.open(Folder.READ_WRITE)

    val messages = folder.search(SubjectTerm(contact.id))
    for (message in messages) {
        message.setFlag(Flags.Flag.DELETED, true)
    }

    folder.close(true)
}