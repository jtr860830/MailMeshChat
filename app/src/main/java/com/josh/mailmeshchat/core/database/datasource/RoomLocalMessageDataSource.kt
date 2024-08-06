package com.josh.mailmeshchat.core.database.datasource

import com.josh.mailmeshchat.core.data.model.Message
import com.josh.mailmeshchat.core.data.model.mapper.toMessage
import com.josh.mailmeshchat.core.data.model.mapper.toMessageEntity
import com.josh.mailmeshchat.core.database.dao.MessageDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomLocalMessageDataSource(
    private val messageDao: MessageDao
) : LocalMessageDataSource {
    override suspend fun insert(message: Message) {
        messageDao.insertMessage(message.toMessageEntity())
    }

    override fun getGroups(): Flow<List<String>> {
        return messageDao.getGroups()
    }

    override fun getMessagesByGroup(subject: String): Flow<List<Message>> {
        return messageDao.getMessagesByGroup(subject)
            .map { messageEntity -> messageEntity.map { it.toMessage() } }
    }
}