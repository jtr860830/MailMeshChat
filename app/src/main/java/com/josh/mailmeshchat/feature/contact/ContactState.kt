@file:OptIn(ExperimentalFoundationApi::class)

package com.josh.mailmeshchat.feature.contact

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.text2.input.TextFieldState
import com.josh.mailmeshchat.core.data.model.Contact
import com.josh.mailmeshchat.core.data.model.ContactSerializable

data class ContactState(
    val search: TextFieldState = TextFieldState(),
    val contacts: List<Contact> = listOf(),
    val isShowCreateGroupDialog: Boolean = false
)