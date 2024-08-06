package com.josh.mailmeshchat.core.data.model.mapper

import com.josh.mailmeshchat.core.data.model.Message
import com.josh.mailmeshchat.core.database.entity.MessageEntity

fun MessageEntity.toMessage(): Message {
    return Message(
        subject = subject,
        sender = sender,
        message = message,
        timestamp = timestamp
    )
}

fun Message.toMessageEntity(): MessageEntity {
    return MessageEntity(
        subject = subject,
        sender = sender,
        message = message,
        timestamp = timestamp
    )
}