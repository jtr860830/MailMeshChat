package com.josh.mailmeshchat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.josh.mailmeshchat.core.designsystem.MailMeshChatTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private val viewModel by viewModel<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                viewModel.state.isCheckingLoginState
            }
        }
        enableEdgeToEdge()
        setContent {
            MailMeshChatTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if(!viewModel.state.isCheckingLoginState) {
                        val navController = rememberNavController()
                        NavigationRoot(
                            navController = navController,
                            viewModel
                        )
                    }
                }
            }
        }
    }
}