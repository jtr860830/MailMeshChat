package com.josh.mailmeshchat.feature.chat

sealed interface ChatEvent {
    data object OnBackClick : ChatEvent
}