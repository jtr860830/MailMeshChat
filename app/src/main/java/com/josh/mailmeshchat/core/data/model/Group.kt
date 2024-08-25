package com.josh.mailmeshchat.core.data.model

data class Group(
    val id: String = "",
    var name: String,
    var members: List<String>,
    var unreadMessageCount: Int? = null,
)
