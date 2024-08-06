@file:OptIn(ExperimentalFoundationApi::class)

package com.josh.mailmeshchat.feature.chat

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.text2.input.TextFieldState
import com.josh.mailmeshchat.core.data.model.Message

data class ChatState(
    val subject: String = "",
    val user: String = "",
    val messages: List<Message> = listOf(),
    val inputMessage: TextFieldState = TextFieldState(),
)
