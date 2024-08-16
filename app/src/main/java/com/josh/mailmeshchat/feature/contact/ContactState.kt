@file:OptIn(ExperimentalFoundationApi::class)

package com.josh.mailmeshchat.feature.contact

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.text2.input.TextFieldState
import com.josh.mailmeshchat.core.data.model.Contact

data class ContactState(
    val search: TextFieldState = TextFieldState(),
    val selectContact: Contact? = null,
    val isFindingGroup: Boolean = false,
    val isShowCreateGroupDialog: Boolean = false,
    val isShowContactDetailDialog: Boolean = false
)