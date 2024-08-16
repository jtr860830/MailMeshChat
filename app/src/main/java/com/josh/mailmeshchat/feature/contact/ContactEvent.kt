package com.josh.mailmeshchat.feature.contact

sealed interface ContactEvent {
    data object LogoutSuccess : ContactEvent
    data class ContactGroupFound(
        val uuid: String,
        val subject: String,
        val user: String
    ) : ContactEvent
}