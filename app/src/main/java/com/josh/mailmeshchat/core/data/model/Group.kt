package com.josh.mailmeshchat.core.data.model

data class Group(
    val id: String = "",
    var name: String,
    val members: List<String>
)
