package com.josh.mailmeshchat.feature.intro

sealed interface IntroAction {
    data object OnSignInClick : IntroAction
}