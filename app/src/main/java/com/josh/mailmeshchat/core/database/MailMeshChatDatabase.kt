package com.josh.mailmeshchat.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.josh.mailmeshchat.core.database.dao.MessageDao
import com.josh.mailmeshchat.core.database.entity.MessageEntity

@Database(entities = [MessageEntity::class], version = 1, exportSchema = false)
abstract class MailMeshChatDatabase : RoomDatabase() {
    abstract fun messageDao(): MessageDao
}