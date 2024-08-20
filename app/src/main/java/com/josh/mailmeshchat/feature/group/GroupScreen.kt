@file:OptIn(ExperimentalFoundationApi::class)

package com.josh.mailmeshchat.feature.group

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.josh.mailmeshchat.MainState
import com.josh.mailmeshchat.MainViewModel
import com.josh.mailmeshchat.R
import com.josh.mailmeshchat.core.designsystem.GroupAddIcon
import com.josh.mailmeshchat.core.designsystem.MailMeshChatTheme
import com.josh.mailmeshchat.core.designsystem.components.GradientBackground
import com.josh.mailmeshchat.core.designsystem.components.MailMeshChatFloatActionButton
import com.josh.mailmeshchat.core.designsystem.components.MailMeshChatToolBar
import com.josh.mailmeshchat.core.ui.ChatGroupItem
import com.josh.mailmeshchat.core.ui.CreateGroupDialog
import com.josh.mailmeshchat.core.ui.PullToRefreshLazyColumn
import com.josh.mailmeshchat.core.util.ObserveAsEvents
import org.koin.androidx.compose.koinViewModel

@Composable
fun GroupScreen(
    onGroupItemClick: (uuid: String, subject: String, userEmail: String) -> Unit,
    viewModel: GroupViewModel = koinViewModel(),
    sharedViewModel: MainViewModel,
    bottomBarPadding: PaddingValues
) {
    LaunchedEffect(key1 = true) {
        sharedViewModel.fetchGroup()
    }

    ObserveAsEvents(flow = viewModel.events) {
        when (it) {
            is GroupEvent.OnGroupItemClick -> onGroupItemClick(it.uuid, it.subject, it.userEmail)
            GroupEvent.OnSwipeRefresh -> sharedViewModel.fetchGroup()
        }
    }

    GroupContent(
        viewModel.state,
        sharedViewModel.state,
        viewModel::onAction,
        bottomBarPadding = bottomBarPadding
    )
}

@Composable
fun GroupContent(
    state: GroupState,
    sharedState: MainState,
    onAction: (GroupAction) -> Unit,
    bottomBarPadding: PaddingValues
) {
    GradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = bottomBarPadding.calculateBottomPadding())
                .padding(bottom = 32.dp)
        ) {
            MailMeshChatToolBar(
                text = stringResource(id = R.string.group),
                icon = null,
                isSearchBar = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            PullToRefreshLazyColumn(
                items = sharedState.groups,
                content = { group ->
                    ChatGroupItem(
                        groupName = group.name,
                        onClick = {
                            onAction(
                                GroupAction.OnGroupItemClick(
                                    uuid = group.id,
                                    subject = group.name
                                )
                            )
                        }
                    )
                },
                isRefreshing = sharedState.isGroupsRefreshing,
                onRefresh = {
                    onAction(GroupAction.OnRefresh)
                },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
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
            icon = GroupAddIcon,
            onClick = { onAction(GroupAction.OnCreateGroupClick) }
        )
    }
    AnimatedVisibility(visible = state.isShowCreateGroupDialog) {
        CreateGroupDialog(
            onDismiss = { onAction(GroupAction.OnCreateGroupDialogDismiss) },
            onSubmit = { name, email -> onAction(GroupAction.OnCreateGroupSubmit(name, email)) }
        )
    }
}

@Preview
@Composable
private fun GroupScreenPreview() {
    MailMeshChatTheme {
        GroupContent(
            state = GroupState(),
            sharedState = MainState(),
            onAction = {},
            PaddingValues()
        )
    }
}