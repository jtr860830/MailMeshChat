package com.josh.mailmeshchat.core.data

import com.josh.mailmeshchat.core.data.model.Contact
import com.josh.mailmeshchat.core.data.model.Group
import com.josh.mailmeshchat.core.data.model.Message
import com.josh.mailmeshchat.core.data.model.UserInfo
import kotlinx.coroutines.flow.Flow

interface MmcRepository {

    suspend fun getUser(): UserInfo?

    suspend fun setUser(info: UserInfo?)

    suspend fun removeUser()

    fun login(userInfo: UserInfo): Boolean

    fun logout()

    suspend fun createContact(contact: Contact)

    fun fetchContacts(): Flow<List<Contact>>

    suspend fun deleteContact(contact: Contact)

    fun observeContacts(): Flow<Unit>

    fun createGroup(to: Array<String>, name: String? = ""): Flow<String>

    suspend fun fetchGroup(): Flow<List<Group>>

    fun observeGroups(): Flow<Unit>

    suspend fun updateGroupMembers(uuid: String, members: List<String>)

    suspend fun replyMessage(subject: String, replyMessage: String)

    suspend fun fetchMessagesBySubject(subject: String): Flow<List<Message>>

    suspend fun observeMessageBySubject(subject: String): Flow<List<Message>>
}