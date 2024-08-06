package com.josh.mailmeshchat

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.josh.mailmeshchat.feature.chat.ChatScreen
import com.josh.mailmeshchat.feature.group.GroupScreen
import com.josh.mailmeshchat.feature.intro.IntroScreen
import com.josh.mailmeshchat.feature.login.LoginScreen

@Composable
fun NavigationRoot(
    navController: NavHostController,
    sharedViewModel: MainViewModel,
) {
    NavHost(
        navController = navController,
        startDestination = if (sharedViewModel.state.isLoggedIn) {
            sharedViewModel.connect()
            "landing"
        } else {
            "onboarding"
        }
    ) {
        onboardingGraph(navController, sharedViewModel)
        landingGraph(navController, sharedViewModel)
    }
}

private fun NavGraphBuilder.onboardingGraph(
    navController: NavHostController,
    sharedViewModel: MainViewModel
) {
    navigation(
        startDestination = "intro",
        route = "onboarding"
    ) {
        composable(route = "intro") {
            IntroScreen(
                onSignInClick = { navController.navigate("login") }
            )
        }
        composable(route = "login") {
            LoginScreen(
                onLoginSuccess = {
                    sharedViewModel.connect()
                    navController.navigate("landing") {
                        popUpTo("login") {
                            inclusive = true
                            saveState = true
                        }
                        restoreState = true
                    }
                }
            )
        }

    }
}

private fun NavGraphBuilder.landingGraph(
    navController: NavHostController,
    sharedViewModel: MainViewModel
) {
    navigation(
        startDestination = "group",
        route = "landing"
    ) {
        composable(route = "group") {
            GroupScreen(
                onLogoutSuccess = {
                    sharedViewModel.disconnect()
                    navController.navigate("onboarding") {
                        popUpTo("landing") {
                            inclusive = true
                        }
                    }
                },
                onGroupItemClick = { subject, user ->
                    navController.navigate("chat/$subject/$user")
                }
            )
        }
        composable(route = "chat/{subject}/{user}") {
            ChatScreen(onBackClick = { navController.popBackStack() })
        }
    }
}