package com.josh.mailmeshchat.core.data

import android.net.Uri
import com.josh.mailmeshchat.core.data.model.Contact
import com.josh.mailmeshchat.core.data.model.Group
import com.josh.mailmeshchat.core.data.model.Message
import com.josh.mailmeshchat.core.data.model.UserInfo
import kotlinx.coroutines.flow.Flow

interface MmcRepository {

    suspend fun createGroup(to: Array<String>, name: String? = "")

    suspend fun fetchGroup(): Flow<List<Group>>

    suspend fun fetchMessagesBySubject(subject: String): Flow<List<Message>>

    suspend fun observeMessageBySubject(subject: String): Flow<List<Message>>

    suspend fun replyMessage(subject: String, replyMessage: String)

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

    fun observeGroups(): Flow<Unit>
}