package com.josh.mailmeshchat.core.designsystem.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.josh.mailmeshchat.core.designsystem.ArrowBackIcon
import com.josh.mailmeshchat.core.designsystem.LogoutIcon
import com.josh.mailmeshchat.core.designsystem.MailMeshChatTheme

@Composable
fun MailMeshChatToolBar(
    modifier: Modifier = Modifier,
    text: String,
    textStyle: TextStyle? = MaterialTheme.typography.headlineMedium,
    icon: ImageVector? = null,
    startIcon: ImageVector? = null,
    onIconClick: () -> Unit = {},
    onStartIconClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .height(56.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        startIcon?.let {
            IconButton(
                modifier = Modifier.padding(start = 4.dp),
                onClick = { onStartIconClick() }
            ) {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = if (startIcon != null) 0.dp else 16.dp),
            style = textStyle!!,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = modifier.weight(1f))
        icon?.let {
            IconButton(
                modifier = Modifier.padding(end = 4.dp),
                onClick = { onIconClick() }
            ) {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

@Preview
@Composable
private fun MailMeshChatToolBarPreview() {
    MailMeshChatTheme {
        MailMeshChatToolBar(
            text = "Chat",
            icon = LogoutIcon,
            startIcon = ArrowBackIcon,
            onIconClick = {}
        )
    }
}