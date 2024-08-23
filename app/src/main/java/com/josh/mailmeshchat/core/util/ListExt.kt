package com.josh.mailmeshchat.core.util

fun MutableList<String>.moveToFirst(userEmail: String): List<String> {
    find { it == userEmail }?.let {
        remove(it)
        add(0, it)
    }
    return this
}