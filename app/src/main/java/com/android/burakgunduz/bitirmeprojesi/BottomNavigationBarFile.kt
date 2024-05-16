package com.android.burakgunduz.bitirmeprojesi

import android.util.Log
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.House
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth

@Composable
fun BottomNavigationBar(
    navController: NavController,
    auth: FirebaseAuth,
    userInfosFar: MutableState<String?>,
    isDarkModeOn: Boolean,
    onDarkModeToggle: (Boolean) -> Unit
) {
    NavigationBar(modifier = Modifier.height(56.dp)) {
        IconButton(onClick = { navController.navigate("feedScreenNav/${auth.currentUser?.uid}") }) {
            Icon(imageVector = Icons.Outlined.House, contentDescription = "Feed")
        }
        IconButton(onClick = { navController.navigate("listItemScreenNav") }) {
            Icon(imageVector = Icons.Outlined.CameraAlt, contentDescription = "Feed")
        }

        Button(onClick = {
            auth.signOut()
            userInfosFar.value = null
            Log.e("UserLogOut", "User is logged out:${userInfosFar.value}")
            navController.navigate("landingPageNav")
        }) {
            Text(text = userInfosFar.value.toString())
        }
        DarkModeToggle(isDarkModeOn = isDarkModeOn, onDarkModeToggle = onDarkModeToggle)


    }
}