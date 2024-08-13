package com.josh.mailmeshchat.core.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Long.toTime(): String {
    val date = Date(this)
    val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
    return sdf.format(date)
}