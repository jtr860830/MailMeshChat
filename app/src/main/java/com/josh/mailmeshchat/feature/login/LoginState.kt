@file:OptIn(ExperimentalFoundationApi::class)

package com.josh.mailmeshchat.feature.login

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.text2.input.TextFieldState

data class LoginState(
    val email: TextFieldState = TextFieldState(),
    val isEmailValid: Boolean = false,
    val password: TextFieldState = TextFieldState(),
    val isPasswordNotEmpty: Boolean = false,
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val canLogin: Boolean = false,
)