package com.josh.mailmeshchat

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.josh.mailmeshchat.core.data.MmcRepository
import com.josh.mailmeshchat.core.sharedpreference.UserStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(
    private val mmcRepository: MmcRepository
) : ViewModel() {

    var state by mutableStateOf(MainState())
        private set

    init {
        viewModelScope.launch {
            state = state.copy(isCheckingLoginState = true)
            state = state.copy(isLoggedIn = mmcRepository.getUser() != null)
            state = state.copy(isCheckingLoginState = false)
        }
    }

    fun connect() {
        viewModelScope.launch(Dispatchers.IO) {
            mmcRepository.connect()
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