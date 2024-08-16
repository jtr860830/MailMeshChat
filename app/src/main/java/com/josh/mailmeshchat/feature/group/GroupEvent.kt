package com.josh.mailmeshchat.feature.group

sealed interface GroupEvent {

    data class OnGroupItemClick(
        val uuid: String,
        val subject: String,
        val userEmail: String
    ) : GroupEvent

    data object OnSwipeRefresh : GroupEvent
}