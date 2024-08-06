package com.josh.mailmeshchat.core.util

fun String.removeAllPrefixes(prefix: String): String {
    var result = this
    while (result.startsWith(prefix)) {
        result = result.removePrefix(prefix)
    }
    return result
}