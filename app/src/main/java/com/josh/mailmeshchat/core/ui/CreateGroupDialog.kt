@file:OptIn(ExperimentalFoundationApi::class)

package com.josh.mailmeshchat.core.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text2.input.TextFieldState
import androidx.compose.foundation.text2.input.clearText
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.josh.mailmeshchat.R
import com.josh.mailmeshchat.core.designsystem.AddIcons
import com.josh.mailmeshchat.core.designsystem.GroupIcon
import com.josh.mailmeshchat.core.designsystem.MailIcon
import com.josh.mailmeshchat.core.designsystem.MailMeshChatTheme
import com.josh.mailmeshchat.core.designsystem.RemoveIcons
import com.josh.mailmeshchat.core.designsystem.components.MailMeshChatActionButton
import com.josh.mailmeshchat.core.designsystem.components.MailMeshChatTextField

@Composable
fun CreateGroupDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (String, String) -> Unit
) {
    if (showDialog) {
        Dialog(onDismissRequest = onDismiss) {
            val name by remember { mutableStateOf(TextFieldState()) }
            val emails = remember { mutableStateListOf(TextFieldState()) }

            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(28.dp))
                    .background(MaterialTheme.colorScheme.background)
                    .padding(vertical = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.enter_group_detail),
                    style = MaterialTheme.typography.headlineMedium,
                    fontSize = 22.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(id = R.string.enter_group_email_description),
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(16.dp))
                MailMeshChatTextField(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    state = name,
                    startIcon = GroupIcon,
                    endIcon = null,
                    hint = stringResource(id = R.string.example_name),
                    title = null
                )
                Spacer(modifier = Modifier.height(16.dp))
                LazyColumn {
                    items(emails.size) { index ->
                        Row(modifier = Modifier, verticalAlignment = Alignment.CenterVertically) {
                            if (index == 0) {
                                IconButton(onClick = {
                                    emails.add(TextFieldState())
                                }, modifier = Modifier.padding(start = 12.dp)) {
                                    Icon(
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .background(color = MaterialTheme.colorScheme.primary),
                                        imageVector = AddIcons,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.background
                                    )
                                }
                            } else {
                                IconButton(onClick = {
                                    val field = emails[index]
                                    field.clearText()
                                    emails.remove(field)
                                }, modifier = Modifier.padding(start = 12.dp)) {
                                    Icon(
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .background(color = MaterialTheme.colorScheme.error),
                                        imageVector = RemoveIcons,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.background
                                    )
                                }
                            }
                            MailMeshChatTextField(
                                modifier = Modifier.padding(end = 24.dp),
                                state = emails[index],
                                startIcon = MailIcon,
                                endIcon = null,
                                hint = stringResource(id = R.string.example_group_email),
                                title = null
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
                MailMeshChatActionButton(
                    modifier = Modifier.padding(horizontal = 40.dp),
                    text = stringResource(id = R.string.submit),
                    isLoading = false,
                    onClick = {
                        val emailString = emails.joinToString(",") { it.text.toString() }
                        if (emailString.isNotEmpty() && name.text.isNotEmpty()) {
                            onSubmit(name.text.toString(), emailString)
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
        CreateGroupDialog(showDialog = true, onDismiss = {}, onSubmit = { _, _ -> })
    }
}