package com.josh.mailmeshchat.core.data.model

data class Message(
    val subject: String,
    val sender: String,
    val message: String,
    val timestamp: Long
)
