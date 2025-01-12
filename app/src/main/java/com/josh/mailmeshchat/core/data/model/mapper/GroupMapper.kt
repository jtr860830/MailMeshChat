package com.josh.mailmeshchat.core.data.model.mapper

import com.josh.mailmeshchat.core.data.model.Group
import com.josh.mailmeshchat.core.data.model.serializable.GroupSerializable
import com.josh.mailmeshchat.core.mailclient.JavaMailClient.Companion.PREFIX_GROUP
import kotlinx.serialization.json.Json
import javax.mail.Message

fun GroupSerializable.toGroup(id: String): Group {
    return Group(
        id = id,
        name = name,
        members = members
    )
}

fun Group.toGroupSerializable(): GroupSerializable {
    return GroupSerializable(
        name = name,
        members = members
    )
}

fun Message.toGroup(): Group {
    val content = content.toString()
    val group = Json.decodeFromString<GroupSerializable>(content)
    return Group(
        id = subject.removePrefix(PREFIX_GROUP),
        name = group.name,
        members = group.members
    )
}