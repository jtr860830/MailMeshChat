package com.josh.mailmeshchat.feature.chat

sealed interface ChatAction {
    data object OnBackClick : ChatAction
    data object OnSendClick : ChatAction
}