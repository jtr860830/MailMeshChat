package com.josh.mailmeshchat.core.data

import com.josh.mailmeshchat.core.data.model.Contact
import com.josh.mailmeshchat.core.data.model.Group
import com.josh.mailmeshchat.core.data.model.Message
import com.josh.mailmeshchat.core.data.model.UserInfo
import com.josh.mailmeshchat.core.data.model.mapper.toGroup
import com.josh.mailmeshchat.core.data.model.mapper.toMessage
import com.josh.mailmeshchat.core.database.datasource.LocalMessageDataSource
import com.josh.mailmeshchat.core.mailclient.JavaMailClient
import com.josh.mailmeshchat.core.mailclient.JavaMailClient.Companion.FOLDER_CONTACTS
import com.josh.mailmeshchat.core.mailclient.JavaMailClient.Companion.FOLDER_GROUPS
import com.josh.mailmeshchat.core.sharedpreference.UserInfoStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DefaultMmcRepository(
    private val userStorage: UserInfoStorage,
    private val mailClient: JavaMailClient,
    private val localMessageDataSource: LocalMessageDataSource
) : MmcRepository {

    override suspend fun createGroup(to: Array<String>, name: String?) {
        mailClient.createGroup(to, name)
    }

    override suspend fun fetchGroup(): Flow<List<Group>> {
        return mailClient.fetchGroups().map {
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

    override suspend fun fetchMessagesBySubject(subject: String): Flow<List<Message>> {
        return mailClient.fetchMessagesBySubject(subject)
            .map { it.map { mimeMessage -> mimeMessage.toMessage() } }
    }

    override suspend fun observeMessageBySubject(subject: String): Flow<List<Message>> {
        return mailClient.observeMessagesBySubject(subject)
            .map { it.map { mimeMessage -> mimeMessage.toMessage() } }
    }

    override suspend fun replyMessage(subject: String, replyMessage: String) {
        mailClient.replyMessage(subject, replyMessage)
    }

    override fun login(userInfo: UserInfo): Boolean {
        return mailClient.login(userInfo)
    }

    override fun logout() {
        mailClient.logout()
    }

    override suspend fun getUser(): UserInfo? {
        return userStorage.get()
    }

    override suspend fun setUser(info: UserInfo?) {
        userStorage.set(info)
    }

    override suspend fun removeUser() {
        userStorage.remove()
    }

    override fun getGroups(): Flow<List<String>> {
        return localMessageDataSource.getGroups()
    }

    override fun getMessagesByGroup(subject: String): Flow<List<Message>> {
        return localMessageDataSource.getMessagesByGroup(subject)
    }

    override suspend fun addContact(contact: Contact) {
        mailClient.addContact(contact)
    }

    override suspend fun deleteContact(contact: Contact) {
        mailClient.deleteContact(contact)
    }

    override fun fetchContacts(): Flow<List<Contact>> {
        return mailClient.fetchContact()
    }

    override fun observeContacts(): Flow<Unit> {
        return mailClient.observeFolder(FOLDER_CONTACTS)
    }

    override fun observeGroups(): Flow<Unit> {
        return mailClient.observeFolder(FOLDER_GROUPS)
    }
}