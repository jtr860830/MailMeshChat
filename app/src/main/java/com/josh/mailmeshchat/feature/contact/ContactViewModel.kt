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

    fun onAction(action: ContactAction) {
        when (action) {
            ContactAction.OnLogoutClick -> {
                viewModelScope.launch(Dispatchers.IO) {
                    mmcRepository.logout()
                    mmcRepository.removeUser()
                    eventChannel.send(ContactEvent.LogoutSuccess)
                }
            }

            ContactAction.OnCreateGroupClick -> showCreateGroupDialog()
            ContactAction.OnCreateGroupDialogDismiss -> hideCreateGroupDialog()

            is ContactAction.OnCreateContactSubmit -> {
                addContact(Contact(name = action.name, email = action.email))
            }

            is ContactAction.OnContactItemClick -> {
                state = state.copy(selectContact = action.contact)
                showContactDetailDialog()
            }

            ContactAction.OnContactDetailDialogDismiss -> hideContactDetailDialog()

            is ContactAction.OnDeleteContactClick -> {
                deleteContact(action.contact)
            }

            is ContactAction.OnSendMessageClick -> {
                sendMessageOrCreateGroup(action.contact)
            }
        }
    }

    private fun sendMessageOrCreateGroup(contact: Contact) {
        state = state.copy(isFindingGroup = true)
        viewModelScope.launch(Dispatchers.IO) {
            val user = mmcRepository.getUser()!!.email
            var groupId = ""
            mmcRepository.fetchGroup().collect { groups ->
                groups.forEach {
                    if (it.members.size == 2 && it.members.contains(user)) {
                        groupId = it.id
                    }
                }
            }
            if (groupId.isEmpty()) {
                createGroup(contact, user)
            } else {
                eventChannel.send(ContactEvent.ContactGroupFound(groupId, contact.name, user))
                hideContactDetailDialog()
            }
        }
    }

    private fun createGroup(contact: Contact, userEmail: String) {
        viewModelScope.launch(Dispatchers.IO) {
            mmcRepository.createGroup(arrayOf(contact.email, userEmail)).collectLatest {
                eventChannel.send(ContactEvent.ContactGroupFound(it, contact.name, userEmail))
                hideContactDetailDialog()
            }
        }
    }

    private fun addContact(contact: Contact) {
        viewModelScope.launch(Dispatchers.IO) {
            mmcRepository.createContact(contact)
        }
    }

    private fun deleteContact(contact: Contact) {
        viewModelScope.launch(Dispatchers.IO) {
            mmcRepository.deleteContact(contact)
        }
    }


    private fun showCreateGroupDialog() {
        state = state.copy(isShowCreateGroupDialog = true)
    }

    private fun hideCreateGroupDialog() {
        state = state.copy(isShowCreateGroupDialog = false)
    }

    private fun showContactDetailDialog() {
        state = state.copy(isShowContactDetailDialog = true)
    }

    private fun hideContactDetailDialog() {
        state = state.copy(isShowContactDetailDialog = false, isFindingGroup = false)
    }
}