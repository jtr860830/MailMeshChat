@file:OptIn(ExperimentalFoundationApi::class)

package com.josh.mailmeshchat.feature.chat

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.josh.mailmeshchat.R
import com.josh.mailmeshchat.core.data.model.Message
import com.josh.mailmeshchat.core.designsystem.ArrowBackIcon
import com.josh.mailmeshchat.core.designsystem.ImageIcon
import com.josh.mailmeshchat.core.designsystem.MailMeshChatTheme
import com.josh.mailmeshchat.core.designsystem.components.GradientBackground
import com.josh.mailmeshchat.core.designsystem.components.MailMeshChatMessageTextField
import com.josh.mailmeshchat.core.designsystem.components.MailMeshChatToolBar
import com.josh.mailmeshchat.core.ui.CurrentUserMessageItem
import com.josh.mailmeshchat.core.ui.OtherUserMessageItem
import com.josh.mailmeshchat.core.util.ObserveAsEvents
import org.koin.androidx.compose.koinViewModel

@Composable
fun ChatScreen(
    onBackClick: () -> Unit,
    viewModel: ChatViewModel = koinViewModel()
) {

    ObserveAsEvents(flow = viewModel.events) { event ->
        when (event) {
            ChatEvent.OnBackClick -> onBackClick()
        }
    }

    ChatContent(
        state = viewModel.state,
        onAction = viewModel::onAction
    )
}

@Composable
fun ChatContent(
    state: ChatState,
    onAction: (ChatAction) -> Unit
) {
    val context = LocalContext.current
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { onAction(ChatAction.OnImageSelected(context, it)) }
    }

    fun launchPhotoPicker() {
        singlePhotoPickerLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    GradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 32.dp)
                .padding(top = 16.dp)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        focusManager.clearFocus()
                    })
                }
        ) {
            MailMeshChatToolBar(
                text = state.subject,
                textStyle = MaterialTheme.typography.bodyMedium,
                startIcon = ArrowBackIcon,
                onStartIconClick = { onAction(ChatAction.OnBackClick) }
            )
            LazyColumn(
                modifier = Modifier.weight(1f),
            ) {
                items(state.messages) { message ->
                    if (message.sender == state.user) {
                        CurrentUserMessageItem(message = message)
                    } else {
                        OtherUserMessageItem(message = message)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            Row(
                modifier = Modifier
                    .padding(start = 4.dp, end = 16.dp, top = 16.dp, bottom = 16.dp)
                    .padding(bottom = 32.dp)
            ) {
                AnimatedVisibility(visible = !state.isTextFieldFocus) {
                    IconButton(onClick = { launchPhotoPicker() }) {
                        Icon(
                            imageVector = ImageIcon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                MailMeshChatMessageTextField(
                    state = state.inputMessage,
                    hint = stringResource(id = R.string.type_a_message),
                    modifier = Modifier
                        .padding(start = if (state.isTextFieldFocus) 16.dp else 4.dp)
                        .focusRequester(focusRequester)
                        .onFocusChanged {
                            onAction(ChatAction.OnTextFieldFocused(it.isFocused))
                        },
                    onSend = { onAction(ChatAction.OnSendClick) }
                )
            }
        }
    }
}

@Preview
@Composable
private fun ChatScreenPreview() {
    MailMeshChatTheme {
        ChatContent(
            state = ChatState(
                subject = "example@gmail.com",
                messages = listOf(Message("", "", "hello", 123)),
                isTextFieldFocus = false
            ),
            onAction = {}
        )
    }
}