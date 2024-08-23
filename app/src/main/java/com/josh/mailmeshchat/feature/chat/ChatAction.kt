package com.josh.mailmeshchat.feature.chat

import android.content.Context
import android.net.Uri

sealed interface ChatAction {
    data object OnBackClick : ChatAction
    data object OnSendClick : ChatAction
    data class OnImageSelected(val context: Context, val image: Uri) : ChatAction
    data class OnTextFieldFocused(val isFocused: Boolean) : ChatAction
    data object OnGroupClick : ChatAction
    data object OnEditGroupMemberDialogDismiss : ChatAction
    data class OnEditGroupMemberSubmit(val members: String) : ChatAction
}