@file:OptIn(ExperimentalFoundationApi::class)

package com.josh.mailmeshchat.core.designsystem.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text2.input.TextFieldState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.josh.mailmeshchat.R
import com.josh.mailmeshchat.core.designsystem.ArrowBackIcon
import com.josh.mailmeshchat.core.designsystem.LogoutIcon
import com.josh.mailmeshchat.core.designsystem.MailMeshChatBlue5
import com.josh.mailmeshchat.core.designsystem.MailMeshChatTheme
import com.josh.mailmeshchat.core.designsystem.SearchIcon

@Composable
fun MailMeshChatToolBar(
    modifier: Modifier = Modifier,
    text: String,
    isSearchBar: Boolean = false,
    search: TextFieldState? = null,
    textStyle: TextStyle? = MaterialTheme.typography.headlineMedium,
    icon: ImageVector? = null,
    startIcon: ImageVector? = null,
    onIconClick: () -> Unit = {},
    onStartIconClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .background(color = MailMeshChatBlue5)
            .padding(top = 48.dp)
    ) {
        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                startIcon?.let {
                    IconButton(onClick = onStartIconClick) {
                        Icon(
                            imageVector = it,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                icon?.let {
                    IconButton(onClick = onIconClick) {
                        Icon(
                            imageVector = it,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = text,
                    style = textStyle!!,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
        if (isSearchBar) {
            MailMeshChatTextField(
                modifier = Modifier.padding(horizontal = 16.dp),
                state = search ?: TextFieldState(),
                startIcon = SearchIcon,
                title = null,
                endIcon = null,
                hint = stringResource(id = R.string.search)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        HorizontalDivider()
    }
}

@Preview
@Composable
private fun MailMeshChatToolBarPreview() {
    MailMeshChatTheme {
        MailMeshChatToolBar(
            text = "Chat",
            isSearchBar = true,
            search = TextFieldState(),
            icon = LogoutIcon,
            startIcon = ArrowBackIcon,
            onIconClick = {}
        )
    }
}