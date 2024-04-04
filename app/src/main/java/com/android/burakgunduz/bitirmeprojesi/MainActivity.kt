package com.android.burakgunduz.bitirmeprojesi

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
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.android.burakgunduz.bitirmeprojesi.feedScreen.FeedScreen
import com.android.burakgunduz.bitirmeprojesi.landingPage.LandingPage
import com.android.burakgunduz.bitirmeprojesi.loginScreen.LoginScreen
import com.android.burakgunduz.bitirmeprojesi.registerScreen.RegisterScreen
import com.android.burakgunduz.bitirmeprojesi.ui.theme.AppTheme
import com.android.burakgunduz.bitirmeprojesi.ui.theme.SuccessButton
import com.android.burakgunduz.bitirmeprojesi.ui.theme.dark_SuccessButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

private lateinit var auth: FirebaseAuth
var storage = Firebase.storage("gs://bitirmeprojesi-1b1e7.appspot.com")
var storageRef = storage.reference

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        setContent {
            var isDarkModeOn by remember { mutableStateOf(true) }
            AppTheme(useDarkTheme = isDarkModeOn) {
                // A surface container using the 'background' color from the theme
                Scaffold { it ->
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
                            onDarkModeToggle = { isDarkModeOn = it })
                    }
                }
            }

        }

    }

}

@Composable
fun OpeningScreen(isDarkModeOn: Boolean, onDarkModeToggle: (Boolean) -> Unit) {
    var db = Firebase.firestore
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val iconSize = (configuration.screenWidthDp.dp * density.density).value.toInt()
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "landingPageNav") {
        composable(
            "feedScreenNav/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) {
            FeedScreen(storageRef, navController)
        }
        composable(
            "loginScreenNav"
        ) {
            LoginScreen(navController, isDarkModeOn, iconSize)
        }
        composable(
            "registerScreenNav"
        ) {
            RegisterScreen(navController, isDarkModeOn, iconSize)
        }
        composable(
            "landingPageNav"
        ) {
            LandingPage(navController, isDarkModeOn, iconSize)
        }
    }
    Row {
        DarkModeToggle(isDarkModeOn = isDarkModeOn, onDarkModeToggle = onDarkModeToggle)
    }
}


fun getAuth(email: String, password: String) {
    val addOnCompleteListener = auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Log.d("LoginSuccess", "signInWithEmail:success")
                val user = auth.currentUser

            } else {
                // If sign in fails, display a message to the user.
                Log.w("LoginFail", "signInWithEmail:failure", task.exception)

            }
        }
}
fun addUserToDatabase(email: String, password: String) {
    val addOnCompleteListener = auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Log.d("RegisterSuccess", "createUserWithEmail:success")
                val user = auth.currentUser

            } else {
                // If sign in fails, display a message to the user.
                Log.w("RegisterFail", "createUserWithEmail:failure", task.exception)

            }
        }
}

@Preview(showBackground = true)
@Composable
fun ScreenPreview() {
    AppTheme {
        OpeningScreen(isDarkModeOn = true, onDarkModeToggle = {})
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
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.background, blendMode = BlendMode.Overlay)
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

@Composable
fun successButtonColor(isDarkModeOn: Boolean): ButtonColors {
    return if (isDarkModeOn) {
        ButtonDefaults.buttonColors(containerColor = dark_SuccessButton)
    } else {
        ButtonDefaults.buttonColors(containerColor = SuccessButton)
    }
}