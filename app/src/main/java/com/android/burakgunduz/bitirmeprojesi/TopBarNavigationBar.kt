package com.android.burakgunduz.bitirmeprojesi

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarNavigationBar(
    navController: NavController,
    isDarkModeOn: Boolean,
    onDarkModeToggle: (Boolean) -> Unit
) {
    val pageDestination = remember {
        mutableStateOf("")
    }
    DisposableEffect(navController) {
        val listener = NavController.OnDestinationChangedListener { controller, destination, arguments ->
            pageDestination.value = destination.route ?: ""
        }
        navController.addOnDestinationChangedListener(listener)

        onDispose {
            navController.removeOnDestinationChangedListener(listener)
        }
    }
    CenterAlignedTopAppBar(
        title = { Text(text = pageDestination.value) },
        actions = {
            MessagingListLogo(navController = navController)
        },
        navigationIcon = {
            if (!pageDestination.value.equals("feedScreenNav/{userId}")) {
                IconButton(onClick = {
                    navController.navigateUp()
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        },
    )
}

@Composable
fun MessagingListLogo(navController: NavController) {
    IconButton(onClick = { navController.navigate("messagingListScreenNav") }) {
        Icon(imageVector = Icons.AutoMirrored.Outlined.Chat, contentDescription = "MessagingList")
    }

}