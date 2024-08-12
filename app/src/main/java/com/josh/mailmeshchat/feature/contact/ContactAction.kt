package com.josh.mailmeshchat.feature.contact

import com.josh.mailmeshchat.core.data.model.Contact

sealed interface ContactAction {
    data object OnLogoutClick : ContactAction
    data object OnCreateGroupClick : ContactAction
    data class OnContactItemClick(val contact: Contact) : ContactAction
    data object OnCreateGroupDialogDismiss : ContactAction
    data class OnCreateContactSubmit(val name: String, val email: String) : ContactAction
    data object OnContactDetailDialogDismiss : ContactAction
    data class OnSendMessageClick(val contact: Contact) : ContactAction
    data class OnDeleteContactClick(val contact: Contact) : ContactAction
}