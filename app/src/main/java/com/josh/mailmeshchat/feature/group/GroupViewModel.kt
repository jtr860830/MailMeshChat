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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class GroupViewModel(
    private val mmcRepository: MmcRepository
) : ViewModel() {

    var state by mutableStateOf(GroupState())
        private set

    private val eventChannel = Channel<GroupEvent>()
    val events = eventChannel.receiveAsFlow()

    fun onAction(action: GroupAction) {
        when (action) {
            GroupAction.OnCreateGroupClick -> showCreateGroupDialog()
            GroupAction.OnCreateGroupDialogDismiss -> hideCreateGroupDialog()

            is GroupAction.OnCreateGroupSubmit -> {
                createGroup(action.name, action.email)
            }

            is GroupAction.OnGroupItemClick -> {
                viewModelScope.launch {
                    eventChannel.send(
                        GroupEvent.OnGroupItemClick(
                            action.uuid,
                            action.subject,
                            mmcRepository.getUser()!!.email
                        )
                    )
                }
            }

            GroupAction.OnRefresh -> {
                viewModelScope.launch {
                    eventChannel.send(GroupEvent.OnSwipeRefresh)
                }
            }
        }
    }

    private fun createGroup(name: String, email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = mmcRepository.getUser()!!.email
            val group = email.split(",").toMutableList()
            group.add(user)
            mmcRepository.createGroup(name = name, to = group.toTypedArray()).collectLatest {
                hideCreateGroupDialog()
            }
        }
    }

    private fun showCreateGroupDialog() {
        state = state.copy(isShowCreateGroupDialog = true)
    }

    private fun hideCreateGroupDialog() {
        state = state.copy(isShowCreateGroupDialog = false)
    }
}