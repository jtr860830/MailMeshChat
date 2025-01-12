package com.josh.mailmeshchat.feature.group

sealed interface GroupAction {
    data object OnCreateGroupClick : GroupAction
    data class OnGroupItemClick(val uuid: String, val subject: String, val members: String) : GroupAction
    data object OnCreateGroupDialogDismiss : GroupAction
    data class OnCreateGroupSubmit(val name: String, val email: String) : GroupAction
    data object OnRefresh : GroupAction
}