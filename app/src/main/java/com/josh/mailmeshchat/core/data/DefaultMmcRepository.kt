package com.josh.mailmeshchat.core.data

import com.josh.mailmeshchat.core.data.model.Contact
import com.josh.mailmeshchat.core.data.model.Message
import com.josh.mailmeshchat.core.data.model.UserInfo
import com.josh.mailmeshchat.core.database.datasource.LocalMessageDataSource
import com.josh.mailmeshchat.core.mailclient.JavaMailClient
import com.josh.mailmeshchat.core.sharedpreference.UserStorage
import kotlinx.coroutines.flow.Flow

class DefaultMmcRepository(
    private val userStorage: UserStorage,
    private val mailClient: JavaMailClient,
    private val localMessageDataSource: LocalMessageDataSource
) : MmcRepository {

    override suspend fun send(to: String) {
        mailClient.send(to, "Hello", onSendSuccess = {
            localMessageDataSource.insert(it)
        })
    }

    override suspend fun reply(subject: String, replyMessage: String) {
        mailClient.reply(subject, replyMessage, onSendSuccess = {
            localMessageDataSource.insert(it)
        })
    }

    override suspend fun connect() {
        mailClient.connect()
    }

    override suspend fun disconnect() {
        mailClient.disconnect()
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
        return mailClient.observeContact()
    }
}