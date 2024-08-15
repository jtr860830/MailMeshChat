package com.josh.mailmeshchat

import com.josh.mailmeshchat.core.data.model.Contact
import com.josh.mailmeshchat.core.data.model.Group

data class MainState(
    val isLoggedIn: Boolean = false,
    val isCheckingLoginState: Boolean = true,
    val contacts: List<Contact> = listOf(),
    val groups: List<Group> = listOf(),
    val isGroupsRefreshing: Boolean = true,
)
