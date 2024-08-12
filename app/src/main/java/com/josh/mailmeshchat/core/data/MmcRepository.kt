package com.josh.mailmeshchat.core.data

import com.josh.mailmeshchat.core.data.model.Contact
import com.josh.mailmeshchat.core.data.model.Message
import com.josh.mailmeshchat.core.data.model.UserInfo
import kotlinx.coroutines.flow.Flow

interface MmcRepository {

    suspend fun sendMessage(to: String)

    suspend fun fetchMessages(): Flow<List<Message>>

    suspend fun fetchMessagesBySubject(subject: String): Flow<List<Message>>

    suspend fun reply(subject: String, replyMessage: String)

    suspend fun connect()

    suspend fun disconnect()

    suspend fun getUser(): UserInfo?

    suspend fun setUser(info: UserInfo?)

    suspend fun removeUser()

    fun getGroups(): Flow<List<String>>

    fun getMessagesByGroup(subject: String): Flow<List<Message>>

    suspend fun addContact(contact: Contact)

    suspend fun deleteContact(contact: Contact)

    fun fetchContacts(): Flow<List<Contact>>

    fun observeContacts(): Flow<Unit>
}