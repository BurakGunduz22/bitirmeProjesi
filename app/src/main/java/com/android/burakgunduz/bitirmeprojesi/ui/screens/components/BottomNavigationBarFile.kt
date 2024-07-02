package com.android.burakgunduz.bitirmeprojesi.ui.screens.components

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
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person2
import androidx.compose.material.icons.outlined.Search
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
import com.android.burakgunduz.bitirmeprojesi.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

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

    fun getIconColor(route: String): Color {
        return when {
            pageDestination.value == route -> Color(0xFF7203FF)
            isDarkModeOn -> Color.White
            else -> Color.Gray
        }
    }
    fun getBackColor(route: String): Color {
        return when {
            pageDestination.value == route -> Color(0xFF7203FF)
            isDarkModeOn -> Color(0xFF7203FF)
            else -> Color(0xFFD9D9D9)
        }
    }
    fun getCameraColor(route: String): Color {
        return when {
            pageDestination.value == route -> Color(0xFFFFFFFF)
            isDarkModeOn -> Color(0xFF9586A8)
            else -> Color(0xFF9586A8)
        }
    }

    if (pageDestination.value == "feedScreenNav/{userId}"
        || pageDestination.value == "sellerProfileScreen/{sellerProfileID}"
        || pageDestination.value == "favoriteScreenNav"
        || pageDestination.value == "searchScreen"
        || pageDestination.value == "userProfileScreen/{sellerProfileID}"
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
                IconButton(
                    onClick = {
                        if (pageDestination.value != "feedScreenNav/{userId}") {
                            navController.navigate("feedScreenNav/${auth.currentUser?.uid}")
                        }
                    },
                    enabled = pageDestination.value != "feedScreenNav/{userId}"
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Home,
                        contentDescription = "Feed",
                        modifier = Modifier.size(25.dp),
                        tint = getIconColor("feedScreenNav/{userId}")
                    )
                }
                IconButton(
                    onClick = {
                        if (pageDestination.value != "searchScreen") {
                            navController.navigate("searchScreen")
                            Log.e("UserLogOut", "User is logged out:${userInfosFar.value}")
                        }
                    },
                    enabled = pageDestination.value != "searchScreen"
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = "Search",
                        modifier = Modifier.size(25.dp),
                        tint = getIconColor("searchScreen")
                    )
                }
                Box(
                    modifier = Modifier
                        .padding(0.dp)
                        .size(55.dp)
                        .clip(CircleShape)
                        .background(getBackColor("listItemScreenNav"))
                        .clickable {
                            if (pageDestination.value != "listItemScreenNav") {
                                navController.navigate("listItemScreenNav")
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CameraAlt,
                        contentDescription = "List Item",
                        modifier = Modifier.size(35.dp),
                        tint = getCameraColor("listItemScreenNav")
                    )
                }
                IconButton(
                    onClick = {
                        if (pageDestination.value != "favoriteScreenNav") {
                            navController.navigate("favoriteScreenNav")
                            Log.e("favoriteScreenNav", "${userInfosFar.value}")
                        }
                    },
                    enabled = pageDestination.value != "favoriteScreenNav"
                ) {
                    Icon(
                        imageVector = Icons.Outlined.FavoriteBorder,
                        contentDescription = "Profile",
                        modifier = Modifier.size(25.dp),
                        tint = getIconColor("favoriteScreenNav")
                    )
                }
                IconButton(
                    onClick = {
                        if (pageDestination.value != "userProfileScreen/${auth.currentUser?.uid}") {
                            navController.navigate("userProfileScreen/${auth.currentUser?.uid}")
                            Log.e("SellerProfile", "${userInfosFar.value}")
                        }
                    },
                    enabled = pageDestination.value != "userProfileScreen/{sellerProfileID}"
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Person2,
                        contentDescription = "Profile",
                        modifier = Modifier.size(25.dp),
                        tint = getIconColor("userProfileScreen/{sellerProfileID}")
                    )
                }
            }
        }
    }
}
