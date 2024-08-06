@file:OptIn(ExperimentalFoundationApi::class)

package com.josh.mailmeshchat.core.designsystem.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text2.BasicTextField2
import androidx.compose.foundation.text2.input.TextFieldState
import androidx.compose.foundation.text2.input.clearText
import androidx.compose.foundation.text2.input.rememberTextFieldState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.josh.mailmeshchat.core.designsystem.MailMeshChatTheme
import com.josh.mailmeshchat.core.designsystem.SendIcon


@Composable
fun MailMeshChatMessageTextField(
    state: TextFieldState,
    hint: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    onSend: () -> Unit = {}
) {
    var isFocused by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
    ) {
        BasicTextField2(
            state = state,
            textStyle = LocalTextStyle.current.copy(
                color = MaterialTheme.colorScheme.onBackground,
            ),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.onBackground),
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(
                    width = 1.dp,
                    color = if (isFocused) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        Color.Transparent
                    },
                    shape = RoundedCornerShape(12.dp)
                )
                .onFocusChanged {
                    isFocused = it.isFocused
                },
            decorator = { innerBox ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(16.dp))
                    Box(
                        modifier = Modifier.weight(1f),
                    ) {
                        if (state.text.isEmpty() && !isFocused) {
                            Text(
                                text = hint,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        innerBox()
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    IconButton(onClick = {
                        onSend()
                        state.clearText()
                    }) {
                        Icon(
                            imageVector = SendIcon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                }
            }
        )
    }
}

@Preview
@Composable
private fun MailMeshChatMessageTextFieldPreview() {
    MailMeshChatTheme {
        MailMeshChatMessageTextField(
            state = rememberTextFieldState(),
            hint = "example@google.com",
            modifier = Modifier.fillMaxWidth()
        )
    }
}