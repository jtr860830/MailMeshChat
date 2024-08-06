@file:OptIn(ExperimentalFoundationApi::class)

package com.josh.mailmeshchat.core.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text2.input.TextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.josh.mailmeshchat.R
import com.josh.mailmeshchat.core.designsystem.MailIcon
import com.josh.mailmeshchat.core.designsystem.MailMeshChatTheme
import com.josh.mailmeshchat.core.designsystem.components.MailMeshChatActionButton
import com.josh.mailmeshchat.core.designsystem.components.MailMeshChatTextField

@Composable
fun CreateGroupDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit
) {
    if (showDialog) {
        Dialog(onDismissRequest = onDismiss) {
            val email by remember { mutableStateOf(TextFieldState()) }

            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(28.dp))
                    .background(MaterialTheme.colorScheme.background)
                    .padding(24.dp)
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.enter_group_email),
                    style = MaterialTheme.typography.headlineMedium,
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(id = R.string.enter_group_email_description),
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(16.dp))
                MailMeshChatTextField(
                    state = email,
                    startIcon = MailIcon,
                    endIcon = null,
                    hint = stringResource(id = R.string.example_email),
                    title = null
                )
                Spacer(modifier = Modifier.height(32.dp))
                MailMeshChatActionButton(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = stringResource(id = R.string.submit),
                    isLoading = false,
                    onClick = {
                        if (email.text.isNotBlank()) {
                            onSubmit(email.text.toString())
                        }
                        onDismiss()
                    })
            }
        }
    }
}

@Preview
@Composable
private fun CreateGroupDialogPreview() {
    MailMeshChatTheme {
        CreateGroupDialog(showDialog = true, onDismiss = {}, onSubmit = {})
    }
}