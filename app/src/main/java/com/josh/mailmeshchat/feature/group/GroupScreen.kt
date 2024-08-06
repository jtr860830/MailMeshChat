@file:OptIn(ExperimentalFoundationApi::class)

package com.josh.mailmeshchat.feature.group

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import com.josh.mailmeshchat.core.ui.ChatGroupItem
import com.josh.mailmeshchat.core.ui.CreateGroupDialog
import com.josh.mailmeshchat.core.util.ObserveAsEvents
import org.koin.androidx.compose.koinViewModel

@Composable
fun GroupScreen(
    onLogoutSuccess: () -> Unit,
    onGroupItemClick: (subject: String, user: String) -> Unit,
    viewModel: GroupViewModel = koinViewModel()
) {
    ObserveAsEvents(flow = viewModel.events) {
        when (it) {
            GroupEvent.LogoutSuccess -> onLogoutSuccess()
            is GroupEvent.OnGroupItemClick -> onGroupItemClick(it.subject, it.user)
        }
    }

    GroupContent(
        viewModel.state,
        viewModel::onAction
    )
}

@Composable
fun GroupContent(
    state: GroupState,
    onAction: (GroupAction) -> Unit
) {
    GradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 32.dp)
                .padding(top = 16.dp)
        ) {
            MailMeshChatToolBar(
                text = stringResource(id = R.string.group),
                icon = LogoutIcon,
                onIconClick = { onAction(GroupAction.OnLogoutClick) })
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
                items(state.groups) { group ->
                    ChatGroupItem(
                        groupName = group,
                        onClick = { onAction(GroupAction.OnGroupItemClick(group)) }
                    )
                }
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(bottom = 32.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        MailMeshChatFloatActionButton(
            onClick = { onAction(GroupAction.OnCreateGroupClick) }
        )
    }
    if (state.isShowCreateGroupDialog) {
        CreateGroupDialog(
            showDialog = state.isShowCreateGroupDialog,
            onDismiss = { onAction(GroupAction.OnCreateGroupDialogDismiss) },
            onSubmit = { onAction(GroupAction.OnCreateGroupSubmit(it)) }
        )
    }
}

@Preview
@Composable
private fun GroupScreenPreview() {
    MailMeshChatTheme {
        GroupContent(
            state = GroupState(),
            onAction = {}
        )
    }
}