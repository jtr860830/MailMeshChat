package com.josh.mailmeshchat.feature.group

sealed interface GroupEvent {

    data class OnGroupItemClick(
        val uuid: String,
        val subject: String,
        val userEmail: String,
        val members: String
    ) : GroupEvent

    data object OnSwipeRefresh : GroupEvent
}