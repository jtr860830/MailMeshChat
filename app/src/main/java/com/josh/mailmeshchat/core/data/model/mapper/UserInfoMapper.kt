package com.josh.mailmeshchat.core.data.model.mapper

import com.josh.mailmeshchat.core.data.model.UserInfo
import com.josh.mailmeshchat.core.sharedpreference.serializable.UserInfoSerializable

fun UserInfo.toUserInfoSerializable(): UserInfoSerializable {
    return UserInfoSerializable(
        email = email,
        password = password
    )
}

fun UserInfoSerializable.toUserInfo(): UserInfo {
    return UserInfo(
        email = email,
        password = password
    )
}