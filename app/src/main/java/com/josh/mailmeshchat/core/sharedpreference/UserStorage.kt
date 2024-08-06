package com.josh.mailmeshchat.core.sharedpreference

import com.josh.mailmeshchat.core.data.model.UserInfo

interface UserStorage {
    suspend fun get(): UserInfo?
    suspend fun set(info: UserInfo?)
    suspend fun remove()
}