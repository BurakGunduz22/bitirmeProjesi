package com.android.burakgunduz.bitirmeprojesi

import android.content.Context
import android.content.pm.ApplicationInfo
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.android.burakgunduz.bitirmeprojesi.screens.directMessagingScreen.DirectMessagingScreen
import com.android.burakgunduz.bitirmeprojesi.screens.feedScreen.FeedScreen
import com.android.burakgunduz.bitirmeprojesi.screens.itemDetailsPage.ItemDetailsPage
import com.android.burakgunduz.bitirmeprojesi.screens.landingPage.LandingPage
import com.android.burakgunduz.bitirmeprojesi.screens.listItemForSale.ListItemScreen
import com.android.burakgunduz.bitirmeprojesi.screens.loginScreen.LoginScreen
import com.android.burakgunduz.bitirmeprojesi.screens.messagingScreen.MessagingListScreen
import com.android.burakgunduz.bitirmeprojesi.screens.registerScreen.RegisterScreen
import com.android.burakgunduz.bitirmeprojesi.ui.theme.AppTheme
import com.android.burakgunduz.bitirmeprojesi.viewModels.ItemViewModel
import com.android.burakgunduz.bitirmeprojesi.viewModels.LocationViewModel
import com.android.burakgunduz.bitirmeprojesi.viewModels.MessageViewModel
import com.google.firebase.appcheck.AppCheckToken
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

private lateinit var auth: FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appCheckOpened(this)
        getToken(
            successCallback = { token ->
                // Use the token here
                println("App Check token: ${token.token}")
                setTokenAutoRefreshEnabled(true)
            },
            errorCallback = { exception ->
                // Handle the error here
                println("Error getting App Check token: ${exception.message}")
            }
        )
        val storage = Firebase.storage("gs://bitirmeproje-ad56d.appspot.com")
        val storageRef = storage.reference
        auth = Firebase.auth
        val itemViewModel = ItemViewModel()
        val locationViewModel = LocationViewModel()
        val messageViewModel = MessageViewModel()
        setContent {
            var isDarkModeOn by remember { mutableStateOf(true) }
            AppTheme(useDarkTheme = isDarkModeOn) {
                // A surface container using the 'background' color from the theme
                OpeningScreen(
                    isDarkModeOn,
                    { isDarkModeOn = it },
                    itemViewModel,
                    locationViewModel,
                    messageViewModel,
                    storageRef
                )
            }

        }

    }

}

@Composable
fun OpeningScreen(
    isDarkModeOn: Boolean,
    onDarkModeToggle: (Boolean) -> Unit,
    itemViewModel: ItemViewModel,
    locationViewModel: LocationViewModel,
    messageViewModel: MessageViewModel,
    storageRef: StorageReference
) {
    val db = Firebase.firestore
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val iconSize = (configuration.screenWidthDp.dp * density.density).value.toInt()
    val navController = rememberNavController()
    var startNavigateScreen by remember { mutableStateOf("") }
    val userInfosFar = remember { mutableStateOf(auth.currentUser?.uid) }
    if (auth.currentUser != null) {
        startNavigateScreen = "feedScreenNav/${auth.currentUser!!.uid}"
    } else if (auth.currentUser == null) {
        startNavigateScreen = "landingPageNav"
    }
    Log.e("UserInfos", "UserInfos: ${userInfosFar.value}")
    Scaffold(
        topBar = {
            if (userInfosFar.value != null) {
                TopBarNavigationBar(
                    navController = navController,
                    isDarkModeOn = isDarkModeOn,
                    onDarkModeToggle = onDarkModeToggle
                )
            }
        }, bottomBar = {
            if (userInfosFar.value != null) {
                BottomNavigationBar(
                    navController,
                    auth,
                    userInfosFar,
                    isDarkModeOn,
                    onDarkModeToggle
                )
            }
        }) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .paint(
                    painter = painterResource(id = R.drawable.back_ground_effect),
                    contentScale = ContentScale.FillWidth
                ),
            color = MaterialTheme.colorScheme.background

        ) {
            BackgroundImage()
            NavHost(navController = navController, startDestination = startNavigateScreen) {
                composable(
                    "feedScreenNav/{userId}",
                    arguments = listOf(navArgument("userId") { type = NavType.StringType })
                ) {
                    FeedScreen(
                        storageRef,
                        db,
                        navController,
                        isDarkModeOn,
                        itemViewModel,
                        userInfosFar,
                        auth
                    )
                }
                composable(
                    "loginScreenNav"
                ) {
                    LoginScreen(navController, isDarkModeOn, iconSize, auth)
                }
                composable(
                    "registerScreenNav"
                ) {
                    RegisterScreen(navController, isDarkModeOn, iconSize, auth, db)
                }
                composable(
                    "landingPageNav"
                ) {
                    LandingPage(navController, isDarkModeOn, iconSize)
                }
                composable(
                    "itemDetailsPageNav/{itemId}",
                    arguments = listOf(
                        navArgument("itemId") { type = NavType.StringType }
                    )
                ) { navBack ->
                    ItemDetailsPage(navBack, navController, itemViewModel, isDarkModeOn, userInfosFar.value)
                }
                composable("listItemScreenNav") {
                    ListItemScreen(itemViewModel, locationViewModel)
                }
                composable("directMessageToSeller/{receiverID}&{itemID}&{conversationUserID}",
                    arguments = listOf(
                        navArgument("receiverID") { type = NavType.StringType },
                        navArgument("itemID") { type = NavType.StringType },
                        navArgument("conversationUserID") { type = NavType.StringType }
                    )
                ) { navBack ->
                    DirectMessagingScreen(messageViewModel, userInfosFar.value, navBack)
                }
                composable("messagingListScreenNav") {
                    MessagingListScreen(
                        storageRef,
                        messageViewModel,
                        userInfosFar,
                        navController,
                        isDarkModeOn
                    )
                }
            }
        }
    }

}

@Composable
fun BackgroundImage() {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.back_ground_effect),
            contentDescription = "Background Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            colorFilter = ColorFilter.tint(
                MaterialTheme.colorScheme.background,
                blendMode = BlendMode.Overlay
            )
        )
    }
}

@Composable
fun DarkModeToggle(isDarkModeOn: Boolean, onDarkModeToggle: (Boolean) -> Unit) {
    IconToggleButton(checked = isDarkModeOn, onCheckedChange = {
        onDarkModeToggle(it)
    }) {
        Icon(
            imageVector = if (isDarkModeOn) {
                Icons.Filled.LightMode
            } else {
                Icons.Filled.DarkMode
            }, contentDescription = "isDarkModeOn"
        )
    }
}

fun appCheckOpened(context: Context) {
    val isDebuggable = 0 != context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE
    if (isDebuggable) {
        FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
            DebugAppCheckProviderFactory.getInstance(),
            false
        )
    } else {
        FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
            PlayIntegrityAppCheckProviderFactory.getInstance(),
            false
        )
    }
}

fun getToken(
    successCallback: (AppCheckToken) -> Unit,
    errorCallback: (Exception) -> Unit
) {
    FirebaseAppCheck.getInstance()
        .getAppCheckToken(false)
        .addOnSuccessListener { result ->
            successCallback.invoke(result)
        }
        .addOnFailureListener { exception ->
            errorCallback.invoke(exception)
        }
}

fun setTokenAutoRefreshEnabled(isEnabled: Boolean) {
    FirebaseAppCheck.getInstance().setTokenAutoRefreshEnabled(isEnabled)
}