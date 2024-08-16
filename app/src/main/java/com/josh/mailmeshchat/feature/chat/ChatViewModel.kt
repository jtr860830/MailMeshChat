@file:OptIn(ExperimentalFoundationApi::class)

package com.josh.mailmeshchat.feature.chat

import android.graphics.BitmapFactory
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.josh.mailmeshchat.core.data.MmcRepository
import com.josh.mailmeshchat.core.data.model.Message
import com.josh.mailmeshchat.core.mailclient.JavaMailClient.Companion.PREFIX_IMAGE
import com.josh.mailmeshchat.core.util.bitmapToString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ChatViewModel(
    savedStateHandle: SavedStateHandle,
    private val mmcRepository: MmcRepository
) : ViewModel() {

    var state by mutableStateOf(ChatState())
        private set

    private val eventChannel = Channel<ChatEvent>()
    val events = eventChannel.receiveAsFlow()

    private val uuid: String = checkNotNull(savedStateHandle["uuid"])
    private val subject: String = checkNotNull(savedStateHandle["subject"])
    private val user: String = checkNotNull(savedStateHandle["userEmail"])

    init {
        state = state.copy(subject = subject)
        state = state.copy(user = user)

        viewModelScope.launch(Dispatchers.IO) {
            mmcRepository.fetchMessagesBySubject(uuid).collectLatest {
                state = state.copy(messages = it.sortedBy { message -> message.timestamp })
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            mmcRepository.observeMessageBySubject(uuid).collect {
                val newMessages = mutableSetOf<Message>()
                for (message in state.messages) {
                    newMessages.add(message)
                }
                for (message in it) {
                    newMessages.add(message)
                }
                state = state.copy(messages = newMessages.toList())
            }
        }
    }

    fun onAction(action: ChatAction) {
        when (action) {
            ChatAction.OnBackClick -> {
                viewModelScope.launch {
                    eventChannel.send(ChatEvent.OnBackClick)
                }
            }

            is ChatAction.OnSendClick -> {
                val message = state.inputMessage.text.toString()
                viewModelScope.launch(Dispatchers.IO) {
                    mmcRepository.replyMessage(uuid, message)
                }
            }

            is ChatAction.OnImageSelected -> {
                viewModelScope.launch(Dispatchers.IO) {
                    action.image.let {
                        BitmapFactory.decodeStream(
                            action.context.contentResolver.openInputStream(it)
                        )?.let { bitmap ->
                            mmcRepository.replyMessage(
                                uuid,
                                "$PREFIX_IMAGE${bitmapToString(bitmap)}"
                            )
                        }
                    }
                }
            }

            is ChatAction.OnTextFieldFocused -> {
                state = state.copy(isTextFieldFocus = action.isFocused)
            }
        }
    }
}