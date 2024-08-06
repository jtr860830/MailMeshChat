package com.josh.mailmeshchat.feature.login

sealed interface LoginAction {
    data object OnLoginClick : LoginAction
    data object OnTogglePasswordVisibilityClick : LoginAction
}