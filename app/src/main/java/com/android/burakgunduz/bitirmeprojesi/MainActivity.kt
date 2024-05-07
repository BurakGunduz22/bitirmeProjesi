package com.android.burakgunduz.bitirmeprojesi

import android.Manifest.permission.READ_MEDIA_IMAGES
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.android.burakgunduz.bitirmeprojesi.feedScreen.FeedScreen
import com.android.burakgunduz.bitirmeprojesi.itemDetailsPage.ItemDetailsPage
import com.android.burakgunduz.bitirmeprojesi.itemViewModel.ItemViewModel
import com.android.burakgunduz.bitirmeprojesi.landingPage.LandingPage
import com.android.burakgunduz.bitirmeprojesi.listItemForSale.ListItemScreen
import com.android.burakgunduz.bitirmeprojesi.loginScreen.LoginScreen
import com.android.burakgunduz.bitirmeprojesi.registerScreen.RegisterScreen
import com.android.burakgunduz.bitirmeprojesi.ui.theme.AppTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

private lateinit var auth: FirebaseAuth
var storage = Firebase.storage("gs://bitirmeproje-ad56d.appspot.com")
var storageRef = storage.reference

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (ContextCompat.checkSelfPermission(this, READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
            Log.d("Hop", "ListItemScreen: Permission not granted")
            ActivityCompat.requestPermissions(this, arrayOf(READ_MEDIA_IMAGES),0)
        }
        auth = Firebase.auth
        val viewModel = ItemViewModel()
        setContent {
            var isDarkModeOn by remember { mutableStateOf(true) }
            AppTheme(useDarkTheme = isDarkModeOn) {
                // A surface container using the 'background' color from the theme
                Scaffold(bottomBar = { }) { it ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(it)
                            .paint(
                                painter = painterResource(id = R.drawable.back_ground_effect),
                                contentScale = ContentScale.FillWidth
                            ),
                        color = MaterialTheme.colorScheme.background

                    ) {
                        BackgroundImage()
                        OpeningScreen(
                            isDarkModeOn = isDarkModeOn,
                            onDarkModeToggle = { isDarkModeOn = it },
                            viewModel = viewModel)
                    }
                }
            }

        }

    }

}

@Composable
fun OpeningScreen(isDarkModeOn: Boolean, onDarkModeToggle: (Boolean) -> Unit,viewModel: ItemViewModel) {
    val db = Firebase.firestore
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val iconSize = (configuration.screenWidthDp.dp * density.density).value.toInt()
    val navController = rememberNavController()
    var startNavigateScreen by remember { mutableStateOf("") }
    val userInfosFar by remember { mutableStateOf(auth.currentUser?.email.toString()) }
    if (auth.currentUser != null) {
        startNavigateScreen = "feedScreenNav/${auth.currentUser!!.uid}"
    } else if (auth.currentUser == null) {
        startNavigateScreen = "landingPageNav"
    }
    NavHost(navController = navController, startDestination = startNavigateScreen) {
        composable(
            "feedScreenNav/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) {
            FeedScreen(storageRef, db, navController, isDarkModeOn,viewModel)
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
        ) {
            ItemDetailsPage(it,navController,viewModel)
        }
        composable("listItemScreenNav") {
            ListItemScreen(viewModel)
        }
    }
    Row(modifier = Modifier.padding(start = 10.dp)) {
        Button(onClick = { navController.navigate("feedScreenNav/1") }) {
            Text(text = "FeedScreen")
        }
        Button(onClick = { navController.navigate("listItemScreenNav") }) {
            Text(text = "List Item for Sale")
        }
        Button(onClick = {
            auth.signOut()
            navController.navigate("landingPageNav")
        }) {
            Text(text = userInfosFar)
        }
        DarkModeToggle(isDarkModeOn = isDarkModeOn, onDarkModeToggle = onDarkModeToggle)
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
