package com.josh.mailmeshchat.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.josh.mailmeshchat.core.data.model.Message
import com.josh.mailmeshchat.core.designsystem.MailMeshChatBlack
import com.josh.mailmeshchat.core.designsystem.MailMeshChatTheme
import com.josh.mailmeshchat.core.mailclient.JavaMailClient.Companion.PREFIX_IMAGE
import com.josh.mailmeshchat.core.util.stringToByteArray
import com.josh.mailmeshchat.core.util.toTime

@Composable
fun CurrentUserMessageItem(modifier: Modifier = Modifier, message: Message) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(end = 16.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.Bottom
    ) {
        Text(
            modifier = Modifier.padding(end = 8.dp),
            text = message.timestamp.toTime()
        )
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp, bottomStart = 28.dp))
                .background(MaterialTheme.colorScheme.secondary)
                .padding(16.dp)
                .widthIn(max = LocalConfiguration.current.screenWidthDp.dp * 0.5f)
        ) {
            if (message.message.startsWith(PREFIX_IMAGE)) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(stringToByteArray(message.message.removePrefix(PREFIX_IMAGE)))
                        .build(),
                    contentDescription = null,
                )
            } else {
                Text(
                    text = message.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MailMeshChatBlack
                )
            }
        }
    }
}

@Composable
fun OtherUserMessageItem(modifier: Modifier = Modifier, message: Message) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 12.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            Modifier
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp, bottomEnd = 28.dp))
                .background(MaterialTheme.colorScheme.primary)
                .padding(16.dp)
                .widthIn(max = LocalConfiguration.current.screenWidthDp.dp * 0.5f)
        ) {
            if (message.message.startsWith(PREFIX_IMAGE)) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(stringToByteArray(message.message.removePrefix(PREFIX_IMAGE)))
                        .build(),
                    contentDescription = null,
                )
            } else {
                Text(
                    text = message.message.trim(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MailMeshChatBlack
                )
            }
        }
        Text(
            modifier = Modifier.padding(start = 8.dp),
            text = message.timestamp.toTime(),
            maxLines = 1
        )
    }
}

@Preview
@Composable
private fun MessageItemPreview() {
    MailMeshChatTheme {
        Column {
            CurrentUserMessageItem(
                message = Message(
                    "tester1@gmail.com",
                    "tester2@gmail.com",
                    "test message",
                    0
                )
            )
            OtherUserMessageItem(
                message = Message(
                    "tester1@gmail.com",
                    "tester2@gmail.com",
                    "test message",
                    0
                )
            )
        }
    }
}