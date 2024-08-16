package com.josh.mailmeshchat

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.josh.mailmeshchat.core.data.MmcRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainViewModel(
    private val mmcRepository: MmcRepository
) : ViewModel() {

    var state by mutableStateOf(MainState())
        private set

    private var observeContactJob: Job? = null
    private var observeGroupJob: Job? = null

    init {
        viewModelScope.launch {
            state = state.copy(isCheckingLoginState = true)
            state = state.copy(isLoggedIn = mmcRepository.getUser() != null)
            state = state.copy(isCheckingLoginState = false)
        }

        viewModelScope.launch(Dispatchers.IO) {
            if (mmcRepository.getUser() != null) {
                connect()
            }
        }
    }

    fun connect() {
        viewModelScope.launch(Dispatchers.IO) {
            mmcRepository.connect()
        }
    }

    fun fetchContact() {
        viewModelScope.launch(Dispatchers.IO) {
            mmcRepository.fetchContacts().collectLatest {
                state = state.copy(contacts = it)
            }
        }
    }

    fun observeContact() {
        if (observeContactJob == null) {
            observeContactJob = viewModelScope.launch(Dispatchers.IO) {
                mmcRepository.observeContacts().collectLatest {
                    fetchContact()
                }
            }
            observeContactJob?.start()
        }
    }

    fun fetchGroup() {
        state = state.copy(isGroupsRefreshing = true)
        viewModelScope.launch(Dispatchers.IO) {
            mmcRepository.fetchGroup().collect { groups ->
                groups.map {
                    if (it.name.contains("@")) {
                        state.contacts.find { contact ->
                            contact.email == it.name
                        }?.let { contact ->
                            it.name = contact.name
                        }
                    }
                }
                state = state.copy(groups = groups.sortedBy { it.name }, isGroupsRefreshing = false)
            }
        }
    }

    fun observeGroup() {
        if (observeGroupJob == null) {
            observeGroupJob = viewModelScope.launch(Dispatchers.IO) {
                mmcRepository.observeGroups().collectLatest {
                    fetchGroup()
                }
            }
            observeGroupJob?.start()
        }
    }


    fun disconnect() {
        viewModelScope.launch(Dispatchers.IO) {
            mmcRepository.disconnect()
        }
    }

    override fun onCleared() {
        super.onCleared()
        disconnect()
    }
}