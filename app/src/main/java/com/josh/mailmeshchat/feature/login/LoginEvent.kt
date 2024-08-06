package com.josh.mailmeshchat.feature.login

sealed interface LoginEvent {
    data object LoginSuccess: LoginEvent
}