package com.josh.mailmeshchat.core.designsystem.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.josh.mailmeshchat.R
import com.josh.mailmeshchat.core.designsystem.MailMeshChatBlue5

@Composable
fun MailMeshChatBottomBar(
    destinations: List<LandingDestination> = createDestinationList(),
    selectedNavItem: Int,
    onNavigate: (Int, LandingDestination) -> Unit
) {
    NavigationBar(
        containerColor = MailMeshChatBlue5,
    ) {
        destinations.forEachIndexed { index, destination ->
            NavigationBarItem(
                label = {
                    Text(text = stringResource(id = destination.iconTextRes))
                },
                icon = {
                    Column {
                        Icon(
                            painter = if (selectedNavItem == index) {
                                painterResource(id = destination.selectedIcon)
                            } else {
                                painterResource(id = destination.unselectedIcon)
                            },
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                selected = selectedNavItem == index,
                onClick = {
                    onNavigate(index, destination)
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.background
                ),
            )
        }
    }
}

private fun createDestinationList(): List<LandingDestination> {
    return listOf(
        LandingDestination.CONTACT,
        LandingDestination.MESSAGE,
    )
}

enum class LandingDestination(
    val selectedIcon: Int,
    val unselectedIcon: Int,
    val iconTextRes: Int,
) {
    CONTACT(
        selectedIcon = R.drawable.contacts_filled,
        unselectedIcon = R.drawable.contacts,
        iconTextRes = R.string.contacts
    ),
    MESSAGE(
        selectedIcon = R.drawable.message_filled,
        unselectedIcon = R.drawable.message,
        iconTextRes = R.string.group
    )
}