package com.josh.mailmeshchat.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "table_message")
data class MessageEntity(
    val subject: String,
    val sender: String,
    val message: String,
    @PrimaryKey val timestamp: Long
)
