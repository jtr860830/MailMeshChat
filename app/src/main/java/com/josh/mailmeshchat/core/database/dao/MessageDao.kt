package com.josh.mailmeshchat.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.josh.mailmeshchat.core.database.entity.MessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMessage(message: MessageEntity)

    @Query("SELECT subject FROM table_message GROUP BY subject")
    fun getGroups(): Flow<List<String>>

    @Query("SELECT * FROM table_message WHERE subject = :subject ORDER BY timestamp")
    fun getMessagesByGroup(subject: String): Flow<List<MessageEntity>>
}