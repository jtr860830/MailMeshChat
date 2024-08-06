package com.josh.mailmeshchat.core.util.validator

interface PatternValidator {
    fun matches(value: String): Boolean
}