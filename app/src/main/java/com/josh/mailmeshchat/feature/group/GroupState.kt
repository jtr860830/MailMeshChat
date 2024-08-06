@file:OptIn(ExperimentalFoundationApi::class)

package com.josh.mailmeshchat.feature.group

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.text2.input.TextFieldState

data class GroupState(
    val search: TextFieldState = TextFieldState(),
    val groups: List<String> = listOf(),
    val isShowCreateGroupDialog: Boolean = false
)