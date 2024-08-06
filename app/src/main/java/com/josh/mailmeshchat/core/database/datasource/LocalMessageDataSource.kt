package com.josh.mailmeshchat.core.database.datasource

import com.josh.mailmeshchat.core.data.model.Message
import kotlinx.coroutines.flow.Flow

interface LocalMessageDataSource {

    suspend fun insert(message: Message)

    fun getGroups(): Flow<List<String>>

    fun getMessagesByGroup(subject: String): Flow<List<Message>>
}