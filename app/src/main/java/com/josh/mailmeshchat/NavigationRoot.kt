package com.josh.mailmeshchat

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import androidx.navigation.navigation
import com.josh.mailmeshchat.core.designsystem.components.LandingDestination
import com.josh.mailmeshchat.core.designsystem.components.MailMeshChatBottomBar
import com.josh.mailmeshchat.feature.chat.ChatScreen
import com.josh.mailmeshchat.feature.contact.ContactScreen
import com.josh.mailmeshchat.feature.group.GroupScreen
import com.josh.mailmeshchat.feature.intro.IntroScreen
import com.josh.mailmeshchat.feature.login.LoginScreen

@Composable
fun NavigationRoot(
    navController: NavHostController,
    sharedViewModel: MainViewModel,
) {
    var selectedNavItem by remember { mutableIntStateOf(0) }
    NavHost(
        navController = navController,
        startDestination = if (sharedViewModel.state.isLoggedIn) {
            "landing"
        } else {
            "onboarding"
        }
    ) {
        onboardingGraph(navController)
        composable(route = "landing") {
            LandingNavGraph(
                selectedNavItem = selectedNavItem,
                onSelectedItemChange = { selectedNavItem = it },
                sharedViewModel = sharedViewModel,
                rootNavController = navController
            )
        }
        composable(route = "chat/{uuid}/{subject}/{userEmail}") {
            ChatScreen(onBackClick = { navController.popBackStack() })
        }
    }
}

private fun NavGraphBuilder.onboardingGraph(
    navController: NavHostController
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

@Composable
fun LandingNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    rootNavController: NavHostController,
    selectedNavItem: Int,
    onSelectedItemChange: (Int) -> Unit,
    sharedViewModel: MainViewModel
) {
    Scaffold(
        modifier = modifier,
        bottomBar = {
            MailMeshChatBottomBar(selectedNavItem = selectedNavItem) { index, destination ->
                onSelectedItemChange(index)
                navigateToLandingDestination(navController, destination)
            }
        },
    ) { padding ->
        NavHost(navController = navController, startDestination = "contact") {
            composable(route = "message") {
                GroupScreen(
                    onGroupItemClick = { uuid, subject, userEmail ->
                        rootNavController.navigate("chat/$uuid/$subject/$userEmail")
                    },
                    sharedViewModel = sharedViewModel,
                    bottomBarPadding = padding
                )
            }
            composable(route = "contact") {
                ContactScreen(
                    onLogoutSuccess = {
                        rootNavController.navigate("onboarding") {
                            popUpTo("landing") {
                                inclusive = true
                            }
                        }
                    },
                    onGroupItemClick = { uuid, subject, userEmail ->
                        rootNavController.navigate("chat/$uuid/$subject/$userEmail")
                    },
                    sharedViewModel = sharedViewModel,
                    bottomBarPadding = padding
                )
            }
        }
    }
}

fun navigateToLandingDestination(
    navController: NavHostController,
    landingDestination: LandingDestination
) {
    val landingNavOptions = navOptions {
        popUpTo(navController.graph.findStartDestination().id) {
            saveState = true
        }

        launchSingleTop = true
        restoreState = true
    }

    when (landingDestination) {
        LandingDestination.CONTACT -> navController.navigate("contact", landingNavOptions)
        LandingDestination.MESSAGE -> navController.navigate("message", landingNavOptions)
    }
}