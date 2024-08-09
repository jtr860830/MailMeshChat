package com.josh.mailmeshchat.core.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ContactSerializable(val name: String, val email: String)
