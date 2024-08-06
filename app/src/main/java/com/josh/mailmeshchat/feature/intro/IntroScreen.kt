package com.josh.mailmeshchat.feature.intro

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.josh.mailmeshchat.R
import com.josh.mailmeshchat.core.designsystem.ChatIcon
import com.josh.mailmeshchat.core.designsystem.MailMeshChatTheme
import com.josh.mailmeshchat.core.designsystem.components.GradientBackground
import com.josh.mailmeshchat.core.designsystem.components.MailMeshChatActionButton

@Composable
fun IntroScreen(
    onSignInClick: () -> Unit
) {
    IntroContent(
        onAction = { action ->
            when (action) {
                IntroAction.OnSignInClick -> onSignInClick()
            }
        }
    )
}

@Composable
fun IntroContent(
    onAction: (IntroAction) -> Unit
) {
    GradientBackground {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            MailMeshChatLogoVertical()
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(bottom = 48.dp)
        ) {
            Text(
                text = stringResource(id = R.string.welcome_to_mailmeshchat),
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(id = R.string.mailmeshchat_description),
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(32.dp))
            MailMeshChatActionButton(
                text = stringResource(id = R.string.sign_in),
                isLoading = false,
                modifier = Modifier.fillMaxWidth(),
                onClick = { onAction(IntroAction.OnSignInClick) }
            )
        }
    }
}

@Composable
private fun MailMeshChatLogoVertical(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            modifier = Modifier.size(48.dp),
            imageVector = ChatIcon,
            contentDescription = "Logo",
            tint = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(id = R.string.app_name),
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Preview
@Composable
private fun OnBoardScreenPreview() {
    MailMeshChatTheme {
        IntroContent(
            onAction = {}
        )
    }
}