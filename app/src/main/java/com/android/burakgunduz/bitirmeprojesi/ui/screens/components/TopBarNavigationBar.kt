package com.android.burakgunduz.bitirmeprojesi.ui.screens.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.android.burakgunduz.bitirmeprojesi.ui.theme.fonts.archivoFonts

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarNavigationBar(
    navController: NavController
) {
    val pageDestination = remember {
        mutableStateOf("")
    }
    DisposableEffect(navController) {
        val listener =
            NavController.OnDestinationChangedListener { controller, destination, arguments ->
                pageDestination.value = destination.route ?: ""
            }
        navController.addOnDestinationChangedListener(listener)

        onDispose {
            navController.removeOnDestinationChangedListener(listener)
        }
    }
    if (pageDestination.value == "feedScreenNav/{userId}") {
        CenterAlignedTopAppBar(
            title = {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Feed",
                        fontFamily = archivoFonts,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-2).sp,
                        style = MaterialTheme.typography.headlineLarge,
                    )
                }
            },
            actions = {
                MessagingListLogo(navController = navController)
            },
        )
    }
}

@Composable
fun MessagingListLogo(navController: NavController) {
    IconButton(onClick = { navController.navigate("messagingListScreenNav") }) {
        Icon(imageVector = Icons.AutoMirrored.Outlined.Chat, contentDescription = "MessagingList")
    }

}