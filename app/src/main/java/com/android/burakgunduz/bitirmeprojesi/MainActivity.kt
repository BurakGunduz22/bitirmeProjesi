package com.android.burakgunduz.bitirmeprojesi

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.android.burakgunduz.bitirmeprojesi.ui.screens.components.BottomNavigationBar
import com.android.burakgunduz.bitirmeprojesi.ui.screens.components.TopBarNavigationBar
import com.android.burakgunduz.bitirmeprojesi.ui.screens.editItemScreen.EditItemScreen
import com.android.burakgunduz.bitirmeprojesi.ui.screens.favoriteScreen.FavoriteScreen
import com.android.burakgunduz.bitirmeprojesi.ui.screens.feedScreen.FeedScreen
import com.android.burakgunduz.bitirmeprojesi.ui.screens.itemDetailsPage.ItemDetailsPage
import com.android.burakgunduz.bitirmeprojesi.ui.screens.landingPage.LandingPage
import com.android.burakgunduz.bitirmeprojesi.ui.screens.listItemForSale.ListItemScreen
import com.android.burakgunduz.bitirmeprojesi.ui.screens.messagingScreen.MessagingListScreen
import com.android.burakgunduz.bitirmeprojesi.ui.screens.messagingScreen.subScreens.DirectMessagingScreen
import com.android.burakgunduz.bitirmeprojesi.ui.screens.searchScreen.SearchScreen
import com.android.burakgunduz.bitirmeprojesi.ui.screens.sellerProfileScreen.SellerProfileScreen
import com.android.burakgunduz.bitirmeprojesi.ui.screens.userProfileScreen.UserProfileScreen
import com.android.burakgunduz.bitirmeprojesi.ui.screens.userProfileScreen.subScreens.EditUserScreen
import com.android.burakgunduz.bitirmeprojesi.ui.screens.userProfileScreen.subScreens.ReportScreen
import com.android.burakgunduz.bitirmeprojesi.ui.screens.userProfileScreen.subScreens.UserListedItems
import com.android.burakgunduz.bitirmeprojesi.ui.theme.AppTheme
import com.android.burakgunduz.bitirmeprojesi.viewModels.AuthViewModel
import com.android.burakgunduz.bitirmeprojesi.viewModels.ItemViewModel
import com.android.burakgunduz.bitirmeprojesi.viewModels.LocationViewModel
import com.android.burakgunduz.bitirmeprojesi.viewModels.MessageViewModel
import com.android.burakgunduz.bitirmeprojesi.viewModels.ReportViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

private lateinit var auth: FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val windowInsetsController =
            WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
        WindowCompat.setDecorFitsSystemWindows(window, false)
        FirebaseApp.initializeApp(this)
        val storage = Firebase.storage("gs://bitirmeproje-ad56d.appspot.com")
        val storageRef = storage.reference
        auth = Firebase.auth
        val itemViewModel = ItemViewModel()
        val locationViewModel = LocationViewModel()
        val messageViewModel = MessageViewModel()
        val authViewModel = AuthViewModel()
        val reportsViewModel = ReportViewModel()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1
                )
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "15"
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("MainActivity", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            Log.d("MainActivity", "FCM Token: $token")
            // You can send this token to your server or save it in shared preferences
        }

        setContent {
            var isDarkModeOn by remember { mutableStateOf(true) }
            isDarkModeOn = isSystemInDarkTheme()
            AppTheme(useDarkTheme = isDarkModeOn) {

                OpeningScreen(
                    isDarkModeOn,
                    { isDarkModeOn = it },
                    itemViewModel,
                    locationViewModel,
                    messageViewModel,
                    authViewModel,
                    reportsViewModel,
                    storageRef
                )
            }
        }
    }

    private fun reload() {
    }

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            reload()
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
    authViewModel: AuthViewModel,
    reportsViewModel: ReportViewModel,
    storageRef: StorageReference
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val isBottomBarVisible = remember { mutableStateOf(true) }
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val iconSize = (configuration.screenWidthDp.dp * density.density).value.toInt()
    val navController = rememberNavController()
    val startNavigateScreen = remember { mutableStateOf("") }
    val userInfosFar = rememberSaveable { mutableStateOf(auth.currentUser?.uid) }
    Log.e("UserInfos", "UserInfos: ${userInfosFar.value}")
    if (userInfosFar.value != null) {
        startNavigateScreen.value = "feedScreenNav/${userInfosFar.value}"
    } else if (userInfosFar.value == null) {
        startNavigateScreen.value = "landingPageNav"
    }
    Log.e("UserInfos", "UserInfos: ${userInfosFar.value}")
    Scaffold(
        topBar = {
            if (userInfosFar.value != null) {
                TopBarNavigationBar(
                    navController = navController
                )
            }
        },
        bottomBar = {
            if (userInfosFar.value != null) {
                BottomNavigationBar(
                    navController,
                    auth,
                    userInfosFar,
                    isDarkModeOn,
                    onDarkModeToggle,
                    isBottomBarVisible.value,
                )
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    )
    { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .paint(
                    painter = painterResource(id = R.drawable.back_ground_effect),
                    contentScale = ContentScale.FillWidth
                )
                .background(MaterialTheme.colorScheme.background)
        ) {
            BackgroundImage()
            Column {
                NavHost(
                    navController = navController,
                    startDestination = startNavigateScreen.value
                ) {
                    composable(
                        "feedScreenNav/{userId}",
                        arguments = listOf(navArgument("userId") { type = NavType.StringType }),
                        enterTransition = {

                            slideIntoContainer(
                                AnimatedContentTransitionScope.SlideDirection.Left,
                                animationSpec = tween(700)
                            )
                        },
                        exitTransition = {

                            slideOutOfContainer(
                                AnimatedContentTransitionScope.SlideDirection.Left,
                                animationSpec = tween(700)
                            )

                        },
                        popEnterTransition = {

                            slideIntoContainer(
                                AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(700)
                            )

                        },
                        popExitTransition = {

                            slideOutOfContainer(
                                AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(700)
                            )


                        }
                    ) {
                        FeedScreen(
                            navController,
                            isDarkModeOn,
                            itemViewModel,
                            userInfosFar,
                            auth,
                            isBottomBarVisible
                        )
                    }
                    composable(
                        "landingPageNav",
                        enterTransition = {

                            slideIntoContainer(
                                AnimatedContentTransitionScope.SlideDirection.Left,
                                animationSpec = tween(700)
                            )
                        },
                        exitTransition = {

                            slideOutOfContainer(
                                AnimatedContentTransitionScope.SlideDirection.Left,
                                animationSpec = tween(700)
                            )

                        },
                        popEnterTransition = {

                            slideIntoContainer(
                                AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(700)
                            )

                        },
                        popExitTransition = {

                            slideOutOfContainer(
                                AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(700)
                            )


                        }) {
                        LandingPage(
                            navController,
                            isDarkModeOn,
                            iconSize,
                            authViewModel,
                            snackbarHostState
                        )
                    }
                    composable(
                        "itemDetailsPageNav/{itemId}",
                        arguments = listOf(
                            navArgument("itemId") { type = NavType.StringType }
                        ),
                        enterTransition = {

                            slideIntoContainer(
                                AnimatedContentTransitionScope.SlideDirection.Left,
                                animationSpec = tween(700)
                            )
                        },
                        exitTransition = {

                            slideOutOfContainer(
                                AnimatedContentTransitionScope.SlideDirection.Left,
                                animationSpec = tween(700)
                            )

                        },
                        popEnterTransition = {

                            slideIntoContainer(
                                AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(700)
                            )

                        },
                        popExitTransition = {

                            slideOutOfContainer(
                                AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(700)
                            )


                        }
                    ) { navBack ->
                        ItemDetailsPage(
                            navBack,
                            navController,
                            itemViewModel,
                            isDarkModeOn,
                            userInfosFar.value,
                            messageViewModel
                        )
                    }
                    composable("listItemScreenNav",
                        enterTransition = {

                            slideIntoContainer(
                                AnimatedContentTransitionScope.SlideDirection.Left,
                                animationSpec = tween(700)
                            )
                        },
                        exitTransition = {

                            slideOutOfContainer(
                                AnimatedContentTransitionScope.SlideDirection.Left,
                                animationSpec = tween(700)
                            )

                        },
                        popEnterTransition = {

                            slideIntoContainer(
                                AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(700)
                            )

                        },
                        popExitTransition = {

                            slideOutOfContainer(
                                AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(700)
                            )


                        }) {
                        ListItemScreen(
                            itemViewModel,
                            locationViewModel,
                            userInfosFar.value!!,
                            navController,
                            isDarkModeOn
                        )
                    }
                    composable("directMessageToSeller/{receiverID}&{itemID}&{conversationUserID}&{messagerName}&{itemName}",
                        arguments = listOf(
                            navArgument("receiverID") { type = NavType.StringType },
                            navArgument("itemID") { type = NavType.StringType },
                            navArgument("conversationUserID") { type = NavType.StringType },
                            navArgument("messagerName") { type = NavType.StringType },
                            navArgument("itemName") { type = NavType.StringType }
                        ),
                        enterTransition = {

                            slideIntoContainer(
                                AnimatedContentTransitionScope.SlideDirection.Left,
                                animationSpec = tween(700)
                            )
                        },
                        exitTransition = {

                            slideOutOfContainer(
                                AnimatedContentTransitionScope.SlideDirection.Left,
                                animationSpec = tween(700)
                            )

                        },
                        popEnterTransition = {

                            slideIntoContainer(
                                AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(700)
                            )

                        },
                        popExitTransition = {

                            slideOutOfContainer(
                                AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(700)
                            )


                        }
                    ) { navBack ->
                        DirectMessagingScreen(
                            messageViewModel,
                            userInfosFar.value,
                            navBack,
                            navController,
                            itemViewModel
                        )
                    }
                    composable("messagingListScreenNav",
                        enterTransition = {

                            slideIntoContainer(
                                AnimatedContentTransitionScope.SlideDirection.Left,
                                animationSpec = tween(700)
                            )
                        },
                        exitTransition = {

                            slideOutOfContainer(
                                AnimatedContentTransitionScope.SlideDirection.Left,
                                animationSpec = tween(700)
                            )

                        },
                        popEnterTransition = {

                            slideIntoContainer(
                                AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(700)
                            )

                        },
                        popExitTransition = {

                            slideOutOfContainer(
                                AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(700)
                            )


                        }) {
                        MessagingListScreen(
                            storageRef,
                            messageViewModel,
                            itemViewModel,
                            userInfosFar,
                            navController,
                            isDarkModeOn
                        )
                    }
                    composable("sellerProfileScreen/{sellerProfileID}",
                        arguments = listOf(
                            navArgument("sellerProfileID") { type = NavType.StringType }
                        ),
                        enterTransition = {

                            slideIntoContainer(
                                AnimatedContentTransitionScope.SlideDirection.Left,
                                animationSpec = tween(700)
                            )
                        },
                        exitTransition = {

                            slideOutOfContainer(
                                AnimatedContentTransitionScope.SlideDirection.Left,
                                animationSpec = tween(700)
                            )

                        },
                        popEnterTransition = {

                            slideIntoContainer(
                                AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(700)
                            )

                        },
                        popExitTransition = {

                            slideOutOfContainer(
                                AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(700)
                            )


                        }) { navBack ->
                        SellerProfileScreen(
                            navBack,
                            itemViewModel,
                            storageRef,
                            isDarkModeOn,
                            navController,
                            userInfosFar.value!!
                        )
                    }
                    composable("favoriteScreenNav",
                        enterTransition = {

                            slideIntoContainer(
                                AnimatedContentTransitionScope.SlideDirection.Left,
                                animationSpec = tween(700)
                            )
                        },
                        exitTransition = {

                            slideOutOfContainer(
                                AnimatedContentTransitionScope.SlideDirection.Left,
                                animationSpec = tween(700)
                            )

                        },
                        popEnterTransition = {

                            slideIntoContainer(
                                AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(700)
                            )

                        },
                        popExitTransition = {

                            slideOutOfContainer(
                                AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(700)
                            )


                        }) {
                        FavoriteScreen(
                            navController,
                            itemViewModel,
                            isDarkModeOn,
                            userInfosFar.value!!
                        )
                    }
                    composable("userProfileScreen/{sellerProfileID}",
                        arguments = listOf(
                            navArgument("sellerProfileID") { type = NavType.StringType }
                        ),
                        enterTransition = {

                            slideIntoContainer(
                                AnimatedContentTransitionScope.SlideDirection.Left,
                                animationSpec = tween(700)
                            )
                        },
                        exitTransition = {

                            slideOutOfContainer(
                                AnimatedContentTransitionScope.SlideDirection.Left,
                                animationSpec = tween(700)
                            )

                        },
                        popEnterTransition = {

                            slideIntoContainer(
                                AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(700)
                            )

                        },
                        popExitTransition = {

                            slideOutOfContainer(
                                AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(700)
                            )


                        }) { navBack ->
                        UserProfileScreen(
                            navBack,
                            itemViewModel,
                            storageRef,
                            isDarkModeOn,
                            navController,
                            auth,
                            userInfosFar,

                            )
                    }
                    composable("editItemScreenNav/{itemId}", arguments = listOf(
                        navArgument("itemId") { type = NavType.StringType }
                    ),
                        enterTransition = {

                            slideIntoContainer(
                                AnimatedContentTransitionScope.SlideDirection.Left,
                                animationSpec = tween(700)
                            )
                        },
                        exitTransition = {

                            slideOutOfContainer(
                                AnimatedContentTransitionScope.SlideDirection.Left,
                                animationSpec = tween(700)
                            )

                        },
                        popEnterTransition = {

                            slideIntoContainer(
                                AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(700)
                            )

                        },
                        popExitTransition = {

                            slideOutOfContainer(
                                AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(700)
                            )


                        }) { navBack ->
                        EditItemScreen(
                            itemViewModel,
                            navController,
                            navBack,
                            userInfosFar.value,
                            locationViewModel
                        )
                    }
                    composable("searchScreen",
                        enterTransition = {

                            slideIntoContainer(
                                AnimatedContentTransitionScope.SlideDirection.Left,
                                animationSpec = tween(700)
                            )
                        },
                        exitTransition = {

                            slideOutOfContainer(
                                AnimatedContentTransitionScope.SlideDirection.Left,
                                animationSpec = tween(700)
                            )

                        },
                        popEnterTransition = {

                            slideIntoContainer(
                                AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(700)
                            )

                        },
                        popExitTransition = {

                            slideOutOfContainer(
                                AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(700)
                            )


                        }) {
                        SearchScreen(itemViewModel, navController, isDarkModeOn, userInfosFar, auth)
                    }
                    composable("userListedItems/{sellerID}&{userInfosFar}",
                        arguments = listOf(
                            navArgument("sellerID") { type = NavType.StringType },
                            navArgument("userInfosFar") { type = NavType.StringType },
                        ),
                        enterTransition = {

                            slideIntoContainer(
                                AnimatedContentTransitionScope.SlideDirection.Left,
                                animationSpec = tween(700)
                            )
                        },
                        exitTransition = {

                            slideOutOfContainer(
                                AnimatedContentTransitionScope.SlideDirection.Left,
                                animationSpec = tween(700)
                            )

                        },
                        popEnterTransition = {

                            slideIntoContainer(
                                AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(700)
                            )

                        },
                        popExitTransition = {

                            slideOutOfContainer(
                                AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(700)
                            )
                        }
                    ) { navBack ->
                        UserListedItems(
                            itemViewModel = itemViewModel,
                            storageRef = storageRef,
                            isDarkModeOn = isDarkModeOn,
                            navController = navController,
                            backStackEntry = navBack
                        )
                    }
                    composable("reportScreen", enterTransition = {

                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )
                    },
                        exitTransition = {

                            slideOutOfContainer(
                                AnimatedContentTransitionScope.SlideDirection.Left,
                                animationSpec = tween(700)
                            )

                        },
                        popEnterTransition = {

                            slideIntoContainer(
                                AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(700)
                            )

                        },
                        popExitTransition = {

                            slideOutOfContainer(
                                AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(700)
                            )
                        }) {
                        ReportScreen(reportsViewModel, userInfosFar.value ?: "", navController)
                    }
                    composable(route = "editUserPage", enterTransition = {

                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )
                    },
                        exitTransition = {

                            slideOutOfContainer(
                                AnimatedContentTransitionScope.SlideDirection.Left,
                                animationSpec = tween(700)
                            )

                        },
                        popEnterTransition = {
                            slideIntoContainer(
                                AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(700)
                            )
                        },
                        popExitTransition = {

                            slideOutOfContainer(
                                AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(700)
                            )
                        }) {
                        EditUserScreen(viewModel = authViewModel, userID = auth.currentUser?.uid!!, navController = navController)
                    }
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

