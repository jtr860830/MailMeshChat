package com.josh.mailmeshchat.core.util

fun String.removeAllPrefixes(prefix: String): String {
    var result = this
    while (result.startsWith(prefix)) {
        result = result.removePrefix(prefix)
    }
    return result
}

fun String.isGroup(): Boolean {
    val regex = "\\(\\d+\\)".toRegex()
    return regex.containsMatchIn(this)
}

fun String.replaceGroupNumber(newNumber: Int): String {
    val regex = "\\(\\d+\\)".toRegex()
    return regex.replace(this) { "($newNumber)" }
}