@file:OptIn(ExperimentalFoundationApi::class)

package com.josh.mailmeshchat.feature.chat

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.josh.mailmeshchat.core.data.MmcRepository
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

    private val subject: String = checkNotNull(savedStateHandle["subject"])
    private val user: String = checkNotNull(savedStateHandle["user"])

    init {
        state = state.copy(subject = subject)
        state = state.copy(user = user)

        viewModelScope.launch(Dispatchers.IO) {
            mmcRepository.fetchMessagesBySubject(subject).collectLatest {
                state = state.copy(messages = it)
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
                val subject = state.subject
                viewModelScope.launch(Dispatchers.IO) {
                    mmcRepository.reply(subject, message)
                }
            }
        }
    }
}