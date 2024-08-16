@file:OptIn(ExperimentalFoundationApi::class)

package com.josh.mailmeshchat.feature.login

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.josh.mailmeshchat.R
import com.josh.mailmeshchat.core.designsystem.CheckIcon
import com.josh.mailmeshchat.core.designsystem.DomainIcon
import com.josh.mailmeshchat.core.designsystem.MailIcon
import com.josh.mailmeshchat.core.designsystem.MailMeshChatTheme
import com.josh.mailmeshchat.core.designsystem.components.GradientBackground
import com.josh.mailmeshchat.core.designsystem.components.MailMeshChatActionButton
import com.josh.mailmeshchat.core.designsystem.components.MailMeshChatPasswordTextField
import com.josh.mailmeshchat.core.designsystem.components.MailMeshChatTextField
import com.josh.mailmeshchat.core.util.ObserveAsEvents
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = koinViewModel()
) {
    ObserveAsEvents(flow = viewModel.events) { event ->
        when (event) {
            LoginEvent.LoginSuccess -> {
                onLoginSuccess()
            }
        }
    }
    LoginContent(
        state = viewModel.state,
        onAction = viewModel::onAction
    )
}

@Composable
fun LoginContent(
    state: LoginState,
    onAction: (LoginAction) -> Unit
) {
    GradientBackground {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 32.dp)
                .padding(top = 16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.get_started_with_mailmeshchat),
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(id = R.string.login_description),
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(48.dp))
            MailMeshChatTextField(
                state = state.email,
                startIcon = MailIcon,
                endIcon = if (state.isEmailValid) CheckIcon else null,
                hint = stringResource(id = R.string.example_email),
                title = stringResource(id = R.string.email),
                modifier = Modifier.fillMaxWidth(),
                keyboardType = KeyboardType.Email
            )
            Spacer(modifier = Modifier.height(16.dp))
            MailMeshChatPasswordTextField(
                state = state.password,
                isPasswordVisible = state.isPasswordVisible,
                onTogglePasswordVisibility = { onAction(LoginAction.OnTogglePasswordVisibilityClick) },
                hint = stringResource(id = R.string.example_password),
                title = stringResource(id = R.string.password),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            MailMeshChatTextField(
                state = state.host,
                startIcon = DomainIcon,
                endIcon = null,
                hint = stringResource(id = R.string.example_host),
                title = stringResource(id = R.string.host),
                modifier = Modifier.fillMaxWidth(),
                keyboardType = KeyboardType.Email
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        MailMeshChatActionButton(
            text = stringResource(id = R.string.login),
            isLoading = state.isLoading,
            enabled = state.canLogin,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 32.dp),
            onClick = {
                onAction(LoginAction.OnLoginClick)
            }
        )
    }
}

@Preview
@Composable
private fun LoginScreenPreview() {
    MailMeshChatTheme {
        LoginContent(state = LoginState(), onAction = {})
    }
}