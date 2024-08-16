package com.josh.mailmeshchat.core.data.model.mapper

import com.josh.mailmeshchat.core.data.model.UserInfo
import com.josh.mailmeshchat.core.data.model.serializable.UserInfoSerializable

fun UserInfo.toUserInfoSerializable(): UserInfoSerializable {
    return UserInfoSerializable(
        email = email,
        password = password,
        host = host
    )
}

fun UserInfoSerializable.toUserInfo(): UserInfo {
    return UserInfo(
        email = email,
        password = password,
        host = host
    )
}