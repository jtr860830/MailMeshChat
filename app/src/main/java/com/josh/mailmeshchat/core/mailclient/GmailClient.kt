package com.josh.mailmeshchat.core.mailclient

import com.josh.mailmeshchat.core.sharedpreference.UserStorage
import java.util.Properties
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Store

class GmailClient(
    userStorage: UserStorage,
) : JavaMailClient(userStorage) {

    override fun configureSMTP(email: String?, password: String?): Session {
        val properties = Properties().apply {
            put("mail.smtp.auth", "true")
            put("mail.smtp.starttls.enable", "true")
            put("mail.smtp.host", "smtp.gmail.com")
            put("mail.smtp.port", "587")
        }

        return Session.getInstance(properties, object : javax.mail.Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(email, password)
            }
        })
    }

    override fun configureIMAP(): Store {
        val properties = Properties().apply {
            put("mail.store.protocol", "imaps")
            put("mail.imaps.host", "imap.gmail.com")
            put("mail.imaps.port", "993")
            put("mail.imaps.ssl.enable", "true")
        }

        return Session.getDefaultInstance(properties, null).getStore("imaps")
    }
}