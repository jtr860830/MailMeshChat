package com.josh.mailmeshchat.core.designsystem.components

import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.josh.mailmeshchat.core.designsystem.GroupAddIcon
import com.josh.mailmeshchat.core.designsystem.MailMeshChatTheme

@Composable
fun MailMeshChatFloatActionButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    FloatingActionButton(
        modifier = modifier,
        onClick = { onClick() }
    ) {
        Icon(imageVector = GroupAddIcon, contentDescription = null)
    }
}

@Preview
@Composable
private fun MailMeshChatFloatActionButtonPreview() {
    MailMeshChatTheme {
        MailMeshChatFloatActionButton(onClick = {})
    }
}
