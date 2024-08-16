@file:OptIn(DelicateCoroutinesApi::class)

package com.josh.mailmeshchat.core.mailclient

import com.josh.mailmeshchat.core.data.model.UserInfo
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Properties
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Store

class JavaMailClient {

    var smtpSession: Session? = null
        private set
    var userInfo: UserInfo? = null
        private set
    var store: Store? = null
        private set


    fun login(userInfo: UserInfo): Boolean {
        try {
            this.userInfo = userInfo
            smtpSession = configureSMTP(userInfo)
            store = configureIMAP(userInfo)
            store?.connect(userInfo.email, userInfo.password)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (store?.isConnected == true) {
            GlobalScope.launch(Dispatchers.IO) { distribution() }
            return true
        }

        return false
    }

    fun logout() {
        try {
            store?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            smtpSession = null
            userInfo = null
            store = null
        }
    }

    private fun configureSMTP(userInfo: UserInfo): Session {
        val properties = Properties().apply {
            put("mail.smtp.auth", "true")
            put("mail.smtp.starttls.enable", "true")
            put("mail.smtp.host", "smtp.${userInfo.host}")
            put("mail.smtp.port", "587")
        }

        return Session.getInstance(properties, object : javax.mail.Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(userInfo.email, userInfo.password)
            }
        })
    }

    private fun configureIMAP(userInfo: UserInfo): Store {
        val properties = Properties().apply {
            put("mail.store.protocol", "imaps")
            put("mail.imaps.host", "imap.${userInfo.host}")
            put("mail.imaps.port", "993")
            put("mail.imaps.ssl.enable", "true")
        }

        return Session.getDefaultInstance(properties, null).getStore("imaps")
    }

    companion object {
        const val FOLDER_INBOX = "INBOX"
        const val FOLDER_CONTACTS = "mmc/contacts"
        const val FOLDER_MESSAGES = "mmc/messages"
        const val FOLDER_GROUPS = "mmc/groups"

        const val HEADER_ID = "X-MMC-Id"
        const val HEADER_TIMESTAMP = "X-MMC-Timestamp"
        const val HEADER_GROUP = "X-MMC-Group"
    }
}