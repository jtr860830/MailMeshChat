@file:OptIn(ExperimentalFoundationApi::class)

package com.josh.mailmeshchat.feature.contact

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.josh.mailmeshchat.core.data.MmcRepository
import com.josh.mailmeshchat.core.data.model.Contact
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ContactViewModel(
    private val mmcRepository: MmcRepository
) : ViewModel() {

    var state by mutableStateOf(ContactState())
        private set

    private val eventChannel = Channel<ContactEvent>()
    val events = eventChannel.receiveAsFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            mmcRepository.fetchContacts().collectLatest {
                state = state.copy(contacts = it)
            }
        }
    }

    fun onAction(action: ContactAction) {
        when (action) {
            ContactAction.OnLogoutClick -> {
                viewModelScope.launch(Dispatchers.IO) {
                    mmcRepository.removeUser()
                    eventChannel.send(ContactEvent.LogoutSuccess)
                }
            }

            ContactAction.OnCreateGroupClick -> showCreateGroupDialog()
            ContactAction.OnCreateGroupDialogDismiss -> hideCreateGroupDialog()

            is ContactAction.OnCreateContactSubmit -> {
                setContact(Contact(name = action.name, email = action.email))
            }

            is ContactAction.OnContactItemClick -> {
                viewModelScope.launch {
                    eventChannel.send(
                        ContactEvent.OnGroupItemClick(
                            action.contact.name,
                            mmcRepository.getUser()!!.email
                        )
                    )
                }
            }
        }
    }

    private fun setContact(contact: Contact) {
        viewModelScope.launch(Dispatchers.IO) {
            mmcRepository.setContact(contact)
        }
    }


    private fun showCreateGroupDialog() {
        state = state.copy(isShowCreateGroupDialog = true)
    }

    private fun hideCreateGroupDialog() {
        state = state.copy(isShowCreateGroupDialog = false)
    }
}