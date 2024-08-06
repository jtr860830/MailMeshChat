@file:OptIn(ExperimentalFoundationApi::class)

package com.josh.mailmeshchat.feature.chat

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.josh.mailmeshchat.R
import com.josh.mailmeshchat.core.data.model.Message
import com.josh.mailmeshchat.core.designsystem.ArrowBackIcon
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
    GradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 32.dp)
                .padding(top = 16.dp)
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
            MailMeshChatMessageTextField(
                state = state.inputMessage,
                hint = stringResource(id = R.string.type_a_message),
                modifier = Modifier
                    .padding(16.dp)
                    .padding(bottom = 32.dp),
                onSend = { onAction(ChatAction.OnSendClick) }
            )
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
                messages = listOf(Message("", "", "hello", 123))
            ),
            onAction = {}
        )
    }
}