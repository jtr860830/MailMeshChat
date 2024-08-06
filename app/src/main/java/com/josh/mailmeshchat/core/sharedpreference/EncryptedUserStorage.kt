package com.josh.mailmeshchat.core.sharedpreference

import android.content.SharedPreferences
import com.josh.mailmeshchat.core.data.model.UserInfo
import com.josh.mailmeshchat.core.data.model.mapper.toUserInfo
import com.josh.mailmeshchat.core.data.model.mapper.toUserInfoSerializable
import com.josh.mailmeshchat.core.sharedpreference.serializable.UserInfoSerializable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class EncryptedUserStorage(
    private val sharedPreferences: SharedPreferences
) : UserStorage {

    override suspend fun get(): UserInfo? {
        return withContext(Dispatchers.IO) {
            val json = sharedPreferences.getString(KEY_USER_INFO, null)
            json?.let {
                Json.decodeFromString<UserInfoSerializable>(it).toUserInfo()
            }
        }
    }

    override suspend fun set(info: UserInfo?) {
        withContext(Dispatchers.IO) {
            if (info == null) {
                sharedPreferences.edit().remove(KEY_USER_INFO).apply()
                return@withContext
            }
            val json = Json.encodeToString(info.toUserInfoSerializable())
            sharedPreferences.edit().putString(KEY_USER_INFO, json).apply()
        }
    }

    override suspend fun remove() {
        withContext(Dispatchers.IO) {
            sharedPreferences.edit().remove(KEY_USER_INFO).apply()
        }
    }

    companion object {
        private const val KEY_USER_INFO = "KEY_USER_INFO"
    }
}