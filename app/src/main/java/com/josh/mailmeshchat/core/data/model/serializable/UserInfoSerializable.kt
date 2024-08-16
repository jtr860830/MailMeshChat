package com.josh.mailmeshchat.core.data.model.serializable

import kotlinx.serialization.Serializable

@Serializable
data class UserInfoSerializable(
    val email: String,
    val password: String,
    val host: String,
)
