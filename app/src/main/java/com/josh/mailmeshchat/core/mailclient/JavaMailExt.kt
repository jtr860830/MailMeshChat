package com.josh.mailmeshchat.core.mailclient

import android.util.Log
import com.josh.mailmeshchat.core.mailclient.JavaMailClient.Companion.FOLDER_GROUPS
import com.josh.mailmeshchat.core.mailclient.JavaMailClient.Companion.FOLDER_INBOX
import com.josh.mailmeshchat.core.mailclient.JavaMailClient.Companion.FOLDER_MESSAGES
import com.josh.mailmeshchat.core.mailclient.JavaMailClient.Companion.HEADER_GROUP
import com.josh.mailmeshchat.core.mailclient.JavaMailClient.Companion.HEADER_ID
import com.sun.mail.imap.IMAPFolder
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.retryWhen
import javax.mail.Folder
import javax.mail.Message
import javax.mail.event.MessageCountEvent
import javax.mail.event.MessageCountListener

fun JavaMailClient.appendMessage(folderName: String, message: Message) {
    try {
        val folder = getFolder(folderName)
        folder.open(Folder.READ_WRITE)
        folder.appendMessages(arrayOf(message))
        folder.close(false)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun JavaMailClient.getFolder(folderName: String): Folder {
    if (!store!!.isConnected) {
        store!!.connect(userInfo?.email, userInfo?.password)
    }
    val folder = store!!.getFolder(folderName)
    if (!folder.exists()) {
        folder.create(Folder.HOLDS_MESSAGES)
    }
    return folder
}

fun JavaMailClient.observeFolder(folderName: String): Flow<Unit> {
    return callbackFlow {
        var folder: Folder? = null
        var idle = true

        try {
            folder = getFolder(folderName)
            folder.open(Folder.READ_ONLY)

            folder.addMessageCountListener(object : MessageCountListener {
                override fun messagesAdded(e: MessageCountEvent?) {
                    trySend(Unit)
                }

                override fun messagesRemoved(e: MessageCountEvent?) {
                    trySend(Unit)
                }
            })

            while (idle) {
                if (!folder.isOpen) {
                    folder.open(Folder.READ_ONLY)
                }
                try {
                    (folder as IMAPFolder).idle()
                } catch (e: Exception) {
                    Log.e("JavaMail", "IMAP IDLE interrupted: ${e.message}")
                    delay(5000)
                }
            }
        } catch (e: Exception) {
            Log.e("JavaMail", "Error in observeFolder: ${e.message}")
            throw e
        } finally {
            folder?.close(false)
            idle = false
        }

        awaitClose {
            folder?.close(false)
            idle = false
        }
    }.retryWhen { cause, attempt ->
        Log.e("JavaMail", "Retrying observeFolder. Attempt: $attempt, Cause: ${cause.message}")
        delay(attempt * 1000)
        true
    }
}

fun JavaMailClient.distribution() {
    try {
        val folder = store!!.getFolder(FOLDER_INBOX)
        folder.open(Folder.READ_WRITE)

        val messages = folder?.messages
        messages?.let {
            for (message in messages) {
                val isMessage = !message.getHeader(HEADER_ID).isNullOrEmpty()
                if (isMessage) appendMessage(FOLDER_MESSAGES, message)

                val isGroup = !message.getHeader(HEADER_GROUP).isNullOrEmpty()
                if (isGroup) appendMessage(FOLDER_GROUPS, message)
            }
        }

        folder.addMessageCountListener(object : MessageCountListener {
            override fun messagesAdded(e: MessageCountEvent?) {
                e?.messages?.toList()?.let {
                    for (message in it) {
                        val isMessage = !message.getHeader(HEADER_ID).isNullOrEmpty()
                        if (isMessage) appendMessage(FOLDER_MESSAGES, message)

                        val isGroup = !message.getHeader(HEADER_GROUP).isNullOrEmpty()
                        if (isGroup) appendMessage(FOLDER_GROUPS, message)
                    }
                }
            }

            override fun messagesRemoved(e: MessageCountEvent?) {

            }
        })

        while (true) {
            if (!folder.isOpen) break
            (folder as IMAPFolder).idle()
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}