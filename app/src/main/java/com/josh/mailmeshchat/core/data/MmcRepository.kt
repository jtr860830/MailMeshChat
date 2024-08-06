package com.josh.mailmeshchat.core.data

import com.josh.mailmeshchat.core.data.model.Message
import com.josh.mailmeshchat.core.data.model.UserInfo
import kotlinx.coroutines.flow.Flow

interface MmcRepository {

    suspend fun send(to: String)

    suspend fun reply(subject: String, replyMessage: String)

    suspend fun connect()

    suspend fun disconnect()

    suspend fun getUser(): UserInfo?

    suspend fun setUser(info: UserInfo?)

    suspend fun removeUser()

    fun getGroups(): Flow<List<String>>

    fun getMessagesByGroup(subject: String): Flow<List<Message>>
}