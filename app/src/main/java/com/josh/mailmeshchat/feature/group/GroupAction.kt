package com.josh.mailmeshchat.feature.group

sealed interface GroupAction {
    data object OnLogoutClick : GroupAction
    data object OnCreateGroupClick : GroupAction
    data class OnGroupItemClick(val subject: String) : GroupAction
    data object OnCreateGroupDialogDismiss : GroupAction
    data class OnCreateGroupSubmit(val email: String) : GroupAction
}