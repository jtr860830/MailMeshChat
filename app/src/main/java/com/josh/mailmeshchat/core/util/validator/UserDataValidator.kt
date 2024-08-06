package com.josh.mailmeshchat.core.util.validator

class UserDataValidator(
    private val patternValidator: PatternValidator
) {
    fun isValidEmail(email: String): Boolean {
        return patternValidator.matches(email.trim())
    }

    fun isPasswordNotEmpty(password: String): Boolean {
        return password.isNotEmpty()
    }
}