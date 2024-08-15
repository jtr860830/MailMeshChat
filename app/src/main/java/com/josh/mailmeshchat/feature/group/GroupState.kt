@file:OptIn(ExperimentalFoundationApi::class)

package com.josh.mailmeshchat.feature.group

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.text2.input.TextFieldState
import com.josh.mailmeshchat.core.data.model.Group

data class GroupState(
    val search: TextFieldState = TextFieldState(),
    val groups: List<Group> = listOf(),
    val isShowCreateGroupDialog: Boolean = false,
    val isRefreshing: Boolean = true
)