package com.android.burakgunduz.bitirmeprojesi

import android.util.Log
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.House
import androidx.compose.material.icons.outlined.Person2
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun BottomNavigationBar(
    navController: NavController,
    auth: FirebaseAuth,
    userInfosFar: MutableState<String?>,
    isDarkModeOn: Boolean,
    onDarkModeToggle: (Boolean) -> Unit,
    isBottomBarValue: Boolean,
) {
    val animationSpec = spring<IntSize>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMedium
    )
    val pageDestination = remember {
        mutableStateOf("")
    }
    val coroutineContext = rememberCoroutineScope()
    val context = LocalContext.current
    val token = stringResource(R.string.default_web_client_id)
    val gso = GoogleSignInOptions
        .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(token)
        .requestEmail()
        .build()
    val googleSignInClient = GoogleSignIn.getClient(context, gso)
    DisposableEffect(navController) {
        val listener =
            NavController.OnDestinationChangedListener { _, destination, _ ->
                pageDestination.value = destination.route ?: ""
            }
        navController.addOnDestinationChangedListener(listener)

        onDispose {
            navController.removeOnDestinationChangedListener(listener)
        }
    }
    Log.e("pageDestination", "pageDestination: ${pageDestination.value}")

    if (pageDestination.value == "feedScreenNav/{userId}"
        || pageDestination.value == "sellerProfileScreen/{sellerProfileID}"
    ) {

        NavigationBar(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.navigate("feedScreenNav/${auth.currentUser?.uid}") }) {
                    Icon(
                        imageVector = Icons.Outlined.House,
                        contentDescription = "Feed",
                        modifier = Modifier.size(25.dp)
                    )
                }
                IconButton(onClick = {
                    navController.navigate("landingPageNav")
                    coroutineContext.launch {
                        delay(1000)
                        userInfosFar.value = null
                        googleSignInClient.signOut()
                        auth.signOut()
                    }
                    Log.e("UserLogOut", "User is logged out:${userInfosFar.value}")
                })
                {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.Logout,
                        contentDescription = "Logout",
                        modifier = Modifier.size(25.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .padding(0.dp)
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF8A3CCE))
                        .clickable { navController.navigate("listItemScreenNav") },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CameraAlt,
                        contentDescription = "List Item",
                        modifier = Modifier.size(25.dp)

                    )
                }
                IconButton(onClick = {
                    navController.navigate("favoriteScreenNav")
                    Log.e("favoriteScreenNav", "${userInfosFar.value}")
                }) {
                    Icon(
                        imageVector = Icons.Outlined.FavoriteBorder,
                        contentDescription = "Profile",
                        modifier = Modifier.size(25.dp)
                    )
                }
                IconButton(onClick = {
                    navController.navigate("userProfileScreen/${auth.currentUser?.uid}")
                    Log.e("SellerProfile", "${userInfosFar.value}")
                }) {
                    Icon(
                        imageVector = Icons.Outlined.Person2,
                        contentDescription = "Profile",
                        modifier = Modifier.size(25.dp)
                    )
                }
            }


        }
    }
}