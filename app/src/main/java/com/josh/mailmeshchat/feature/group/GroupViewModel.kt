@file:OptIn(ExperimentalFoundationApi::class)

package com.josh.mailmeshchat.feature.group

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.josh.mailmeshchat.core.data.MmcRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class GroupViewModel(
    private val mmcRepository: MmcRepository
) : ViewModel() {

    var state by mutableStateOf(GroupState())
        private set

    private val eventChannel = Channel<GroupEvent>()
    val events = eventChannel.receiveAsFlow()

    init {
        mmcRepository.getGroups().onEach { groups ->
            state = state.copy(groups = groups)
        }.launchIn(viewModelScope)
    }

    fun onAction(action: GroupAction) {
        when (action) {
            GroupAction.OnLogoutClick -> {
                viewModelScope.launch(Dispatchers.IO) {
                    mmcRepository.removeUser()
                    eventChannel.send(GroupEvent.LogoutSuccess)
                }
            }

            GroupAction.OnCreateGroupClick -> showCreateGroupDialog()
            GroupAction.OnCreateGroupDialogDismiss -> hideCreateGroupDialog()

            is GroupAction.OnCreateGroupSubmit -> {
                createGroup(action.email)
            }

            is GroupAction.OnGroupItemClick -> {
                viewModelScope.launch {
                    eventChannel.send(
                        GroupEvent.OnGroupItemClick(
                            action.subject,
                            mmcRepository.getUser()!!.email
                        )
                    )
                }
            }
        }
    }

    private fun createGroup(email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            mmcRepository.send(email)
        }
    }

    private fun showCreateGroupDialog() {
        state = state.copy(isShowCreateGroupDialog = true)
    }

    private fun hideCreateGroupDialog() {
        state = state.copy(isShowCreateGroupDialog = false)
    }
}