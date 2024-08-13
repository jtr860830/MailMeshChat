package com.josh.mailmeshchat.core.data.model.serializable

import kotlinx.serialization.Serializable

@Serializable
data class GroupSerializable(val name: String, val members: List<String>)
