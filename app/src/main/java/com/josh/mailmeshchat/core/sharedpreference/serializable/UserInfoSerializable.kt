package com.josh.mailmeshchat.core.sharedpreference.serializable

import kotlinx.serialization.Serializable

@Serializable
data class UserInfoSerializable(
    val email: String,
    val password: String
)
