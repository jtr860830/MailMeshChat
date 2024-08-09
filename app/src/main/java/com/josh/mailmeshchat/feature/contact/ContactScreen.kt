@file:OptIn(ExperimentalFoundationApi::class)

package com.josh.mailmeshchat.feature.contact

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.josh.mailmeshchat.R
import com.josh.mailmeshchat.core.designsystem.LogoutIcon
import com.josh.mailmeshchat.core.designsystem.MailMeshChatTheme
import com.josh.mailmeshchat.core.designsystem.SearchIcon
import com.josh.mailmeshchat.core.designsystem.components.GradientBackground
import com.josh.mailmeshchat.core.designsystem.components.MailMeshChatFloatActionButton
import com.josh.mailmeshchat.core.designsystem.components.MailMeshChatTextField
import com.josh.mailmeshchat.core.designsystem.components.MailMeshChatToolBar
import com.josh.mailmeshchat.core.ui.ContactItem
import com.josh.mailmeshchat.core.ui.CreateContactDialog
import com.josh.mailmeshchat.core.util.ObserveAsEvents
import org.koin.androidx.compose.koinViewModel

@Composable
fun ContactScreen(
    onLogoutSuccess: () -> Unit,
    onGroupItemClick: (subject: String, user: String) -> Unit,
    viewModel: ContactViewModel = koinViewModel(),
    bottomBarPadding: PaddingValues
) {
    ObserveAsEvents(flow = viewModel.events) {
        when (it) {
            ContactEvent.LogoutSuccess -> onLogoutSuccess()
            is ContactEvent.OnGroupItemClick -> onGroupItemClick(it.subject, it.user)
        }
    }

    ContactContent(
        viewModel.state,
        viewModel::onAction,
        bottomBarPadding = bottomBarPadding
    )
}

@Composable
fun ContactContent(
    state: ContactState,
    onAction: (ContactAction) -> Unit,
    bottomBarPadding: PaddingValues
) {
    GradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = bottomBarPadding.calculateBottomPadding())
                .padding(vertical = 32.dp)
                .padding(top = 16.dp)
        ) {
            MailMeshChatToolBar(
                text = stringResource(id = R.string.contacts),
                icon = LogoutIcon,
                onIconClick = { onAction(ContactAction.OnLogoutClick) })
            Spacer(modifier = Modifier.height(8.dp))
            MailMeshChatTextField(
                modifier = Modifier.padding(horizontal = 16.dp),
                state = state.search,
                startIcon = SearchIcon,
                title = null,
                endIcon = null,
                hint = stringResource(id = R.string.search)
            )
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                items(state.contacts) { contact ->
                    ContactItem(
                        name = contact.name,
                        onClick = { onAction(ContactAction.OnContactItemClick(contact)) }
                    )
                }
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = bottomBarPadding.calculateBottomPadding())
            .padding(16.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        MailMeshChatFloatActionButton(
            onClick = { onAction(ContactAction.OnCreateGroupClick) }
        )
    }
    if (state.isShowCreateGroupDialog) {
        CreateContactDialog(
            showDialog = state.isShowCreateGroupDialog,
            onDismiss = { onAction(ContactAction.OnCreateGroupDialogDismiss) },
            onSubmit = { name, email -> onAction(ContactAction.OnCreateContactSubmit(name, email)) }
        )
    }
}

@Preview
@Composable
private fun ContactScreenPreview() {
    MailMeshChatTheme {
        ContactContent(
            state = ContactState(),
            onAction = {},
            PaddingValues()
        )
    }
}