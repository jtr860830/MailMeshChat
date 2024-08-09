@file:OptIn(ExperimentalFoundationApi::class)

package com.josh.mailmeshchat.feature.login

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.text2.input.textAsFlow
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.josh.mailmeshchat.core.data.MmcRepository
import com.josh.mailmeshchat.core.data.model.UserInfo
import com.josh.mailmeshchat.core.util.validator.UserDataValidator
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val userDataValidator: UserDataValidator,
    private val mmcRepository: MmcRepository
) : ViewModel() {

    var state by mutableStateOf(LoginState())
        private set

    private val eventChannel = Channel<LoginEvent>()
    val events = eventChannel.receiveAsFlow()

    init {
        state.email.textAsFlow()
            .onEach { email ->
                val isEmailValid = userDataValidator.isValidEmail(email.toString())
                state = state.copy(
                    isEmailValid = isEmailValid,
                    canLogin = isEmailValid && state.isPasswordNotEmpty
                )
            }
            .launchIn(viewModelScope)

        state.password.textAsFlow()
            .onEach { password ->
                val isPasswordNotEmpty = userDataValidator.isPasswordNotEmpty(password.toString())
                state = state.copy(
                    isPasswordNotEmpty = isPasswordNotEmpty,
                    canLogin = state.isEmailValid && isPasswordNotEmpty
                )
            }
            .launchIn(viewModelScope)
    }

    fun onAction(action: LoginAction) {
        when (action) {
            LoginAction.OnLoginClick -> {
                login()
            }

            LoginAction.OnTogglePasswordVisibilityClick -> {
                state = state.copy(
                    isPasswordVisible = !state.isPasswordVisible
                )
            }
        }
    }

    private fun login() {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            mmcRepository.setUser(UserInfo(state.email.text.toString(), state.password.text.toString()))
            delay(1000L)
            state = state.copy(isLoading = false)
            eventChannel.send(LoginEvent.LoginSuccess)
        }
    }
}