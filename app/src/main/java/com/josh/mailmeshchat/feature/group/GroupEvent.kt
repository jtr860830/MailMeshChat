package com.josh.mailmeshchat.feature.group

sealed interface GroupEvent {
    data object LogoutSuccess : GroupEvent
    data class OnGroupItemClick(val subject: String, val user: String) : GroupEvent
}