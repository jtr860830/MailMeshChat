package com.josh.mailmeshchat.core.data

import com.josh.mailmeshchat.core.data.model.Contact
import com.josh.mailmeshchat.core.data.model.Group
import com.josh.mailmeshchat.core.data.model.Message
import com.josh.mailmeshchat.core.data.model.UserInfo
import com.josh.mailmeshchat.core.data.model.mapper.toGroup
import com.josh.mailmeshchat.core.data.model.mapper.toMessage
import com.josh.mailmeshchat.core.mailclient.JavaMailClient
import com.josh.mailmeshchat.core.mailclient.JavaMailClient.Companion.FOLDER_CONTACTS
import com.josh.mailmeshchat.core.mailclient.JavaMailClient.Companion.FOLDER_GROUPS
import com.josh.mailmeshchat.core.mailclient.createContact
import com.josh.mailmeshchat.core.mailclient.createGroup
import com.josh.mailmeshchat.core.mailclient.deleteContact
import com.josh.mailmeshchat.core.mailclient.fetchContact
import com.josh.mailmeshchat.core.mailclient.fetchGroups
import com.josh.mailmeshchat.core.mailclient.fetchMessagesBySubject
import com.josh.mailmeshchat.core.mailclient.observeFolder
import com.josh.mailmeshchat.core.mailclient.observeMessagesBySubject
import com.josh.mailmeshchat.core.mailclient.replyMessage
import com.josh.mailmeshchat.core.mailclient.updateGroupMembers
import com.josh.mailmeshchat.core.sharedpreference.UserInfoStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DefaultMmcRepository(
    private val userStorage: UserInfoStorage,
    private val mailClient: JavaMailClient,
) : MmcRepository {

    override suspend fun getUser(): UserInfo? {
        return userStorage.get()
    }

    override suspend fun setUser(info: UserInfo?) {
        userStorage.set(info)
    }

    override suspend fun removeUser() {
        userStorage.remove()
    }

    override fun login(userInfo: UserInfo): Boolean {
        return mailClient.login(userInfo)
    }

    override fun logout() {
        mailClient.logout()
    }

    override suspend fun createContact(contact: Contact) {
        mailClient.createContact(contact)
    }

    override fun fetchContacts(): Flow<List<Contact>> {
        return mailClient.fetchContact()
    }

    override suspend fun deleteContact(contact: Contact) {
        mailClient.deleteContact(contact)
    }

    override fun observeContacts(): Flow<Unit> {
        return mailClient.observeFolder(FOLDER_CONTACTS)
    }

    override fun createGroup(to: Array<String>, name: String?): Flow<String> {
        return mailClient.createGroup(to, name)
    }

    override suspend fun fetchGroup(): Flow<List<Group>> {
        val userEmail = userStorage.get()?.email ?: ""
        return mailClient.fetchGroups(userEmail).map {
            it.map { mimeMessage ->
                val group = mimeMessage.toGroup()
                if (group.name.isEmpty()) {
                    group.name = group.members
                        .filter { email -> email != userStorage.get()?.email }
                        .joinToString()
                }
                group
            }
        }
    }

    override fun observeGroups(): Flow<Unit> {
        return mailClient.observeFolder(FOLDER_GROUPS)
    }

    override suspend fun updateGroupMembers(uuid: String, members: List<String>) {
        mailClient.updateGroupMembers(uuid, members)
    }

    override suspend fun replyMessage(subject: String, replyMessage: String) {
        mailClient.replyMessage(subject, replyMessage)
    }

    override suspend fun fetchMessagesBySubject(subject: String): Flow<List<Message>> {
        return mailClient.fetchMessagesBySubject(subject)
            .map { it.map { mimeMessage -> mimeMessage.toMessage() } }
    }

    override suspend fun observeMessageBySubject(subject: String): Flow<List<Message>> {
        return mailClient.observeMessagesBySubject(subject)
            .map { it.map { mimeMessage -> mimeMessage.toMessage() } }
    }
}