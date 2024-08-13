package com.josh.mailmeshchat.core.data.model.mapper

import com.josh.mailmeshchat.core.data.model.Contact
import com.josh.mailmeshchat.core.data.model.serializable.ContactSerializable

fun ContactSerializable.toContact(id: String): Contact {
    return Contact(
        id = id,
        name = name,
        email = email
    )
}

fun Contact.toContactSerializable(): ContactSerializable {
    return ContactSerializable(
        name = name,
        email = email
    )
}