package com.josh.mailmeshchat.feature.contact

sealed interface ContactEvent {
    data object LogoutSuccess : ContactEvent
    data class OnGroupItemClick(
        val uuid: String,
        val subject: String,
        val user: String
    ) : ContactEvent
}