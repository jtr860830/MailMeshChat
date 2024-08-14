package com.josh.mailmeshchat.core.util

import android.util.Base64

fun byteArrayToString(bytes: ByteArray): String {
    return Base64.encodeToString(bytes, Base64.DEFAULT)
}

fun stringToByteArray(str: String): ByteArray {
    return Base64.decode(str, Base64.DEFAULT)
}