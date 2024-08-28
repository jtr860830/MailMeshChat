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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MainViewModel(
    private val mmcRepository: MmcRepository
) : ViewModel() {

    var state by mutableStateOf(MainState())
        private set

    private var observeContactJob: Job? = null

    init {
        viewModelScope.launch {
            state = state.copy(isCheckingLoginState = true)
            state = state.copy(isLoggedIn = mmcRepository.getUser() != null)
            state = state.copy(isCheckingLoginState = false)
        }

        viewModelScope.launch(Dispatchers.IO) {
            mmcRepository.getUser()?.let {
                mmcRepository.login(it)
            }
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
        viewModelScope.launch(Dispatchers.IO) {
            state = state.copy(isGroupsRefreshing = true)
            mmcRepository.fetchGroupAndUnreadMessageCount(state.contacts)
                .map { it.sortedBy { group -> group.name } }.collect { sortedGroups ->
                state = state.copy(groups = sortedGroups, isGroupsRefreshing = false)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        mmcRepository.logout()
    }
}