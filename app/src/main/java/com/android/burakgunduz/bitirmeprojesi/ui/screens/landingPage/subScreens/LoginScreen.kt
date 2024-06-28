package com.android.burakgunduz.bitirmeprojesi.ui.screens.landingPage.subScreens

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.android.burakgunduz.bitirmeprojesi.ui.screens.components.MainButtonForAuth
import com.android.burakgunduz.bitirmeprojesi.ui.screens.components.SnackBarFile
import com.android.burakgunduz.bitirmeprojesi.ui.screens.components.TextFieldForAuth
import com.android.burakgunduz.bitirmeprojesi.ui.screens.components.authKeyboardType
import com.android.burakgunduz.bitirmeprojesi.ui.screens.components.GoogleAuthButton
import com.android.burakgunduz.bitirmeprojesi.ui.theme.fonts.archivoFonts
import com.android.burakgunduz.bitirmeprojesi.viewModels.AuthViewModel
import kotlinx.coroutines.CoroutineScope


@Composable
fun LoginScreen(
    navController: NavController,
    isDarkModeOn: Boolean,
    cardExpanded: MutableState<Boolean>,
    iconSize: Int,
    screenNumber: MutableState<Int>,
    authViewModel: AuthViewModel,
    isLoading: MutableState<Boolean>,
    snackbarHostState: SnackbarHostState,
) {
    var mailInputString by remember { mutableStateOf("") }
    var passwordInputString by remember { mutableStateOf("") }
    val invalidUserInfo = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Card(
                shape = CircleShape,
                modifier = Modifier
                    .size(iconSize.dp / 10)
                    .border(2.dp, Color.Gray, CircleShape), // Add this line to create a border
                colors = iconCardColor(isDarkModeOn)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = "",
                        modifier = Modifier
                            .size(iconSize.dp / 8)
                            .padding(3.dp),
                    )
                }
            }
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Login",
                    fontFamily = archivoFonts,
                    fontWeight = FontWeight.ExtraBold,
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Left,
                    modifier = Modifier.padding(start = 20.dp, top = 10.dp, bottom = 5.dp),
                    letterSpacing = (-1).sp,
                )
            }
        }
        TextFieldForAuth(
            takeAuthValue = mailInputString,
            labelText = "Email",
            keyboardOpt = authKeyboardType("email"),
            focusManager = focusManager
        ) {
            mailInputString = it
        }
        TextFieldForAuth(
            takeAuthValue = passwordInputString,
            labelText = "Password",
            authKeyboardType("password"),
            focusManager = focusManager,
            fieldCount = 2
        ) {
            passwordInputString = it
        }
        MainButtonForAuth(
            action = {
                login(
                    mailInputString,
                    passwordInputString,
                    navController,
                    invalidUserInfo.value,
                    authViewModel,
                    snackbarHostState,
                    coroutineScope
                )
            },
            isDarkModeOn = isDarkModeOn,
            buttonText = "LOGIN"
        )
        GoogleAuthButton(navController, isDarkModeOn, "Login with Google")
        TextButton(onClick = {
            screenNumber.value = 3
            isLoading.value = true
        }) {
            Text(
                text = "FORGOT PASSWORD?",
                fontFamily = archivoFonts,
                fontWeight = FontWeight.ExtraBold,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "DON'T YOU HAVE AN ACCOUNT?",
                fontFamily = archivoFonts,
                fontWeight = FontWeight.ExtraBold,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
            )
            TextButton(onClick = {
                screenNumber.value = 2
                cardExpanded.value = !cardExpanded.value
                isLoading.value = true
            }) {
                Text(
                    text = "SIGN UP",
                    textDecoration = TextDecoration.Underline,
                    fontFamily = archivoFonts,
                    fontWeight = FontWeight.ExtraBold,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

fun login(
    mailInputString: String,
    passwordInputString: String,
    navController: NavController,
    invalidUserInfo: Boolean,
    authViewModel: AuthViewModel,
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope
) {
    if (mailInputString.isBlank() || passwordInputString.isBlank()) {
        SnackBarFile(
            coroutineScope,
            snackbarHostState,
            "Email or password cannot be empty",
            "Short"
        )
        Log.e("LoginScreen", "Email or password cannot be empty")
    } else {
        authViewModel.getAuth(
            mailInputString,
            passwordInputString,
        ) { isCompleted, userID ->
            if (!isCompleted) {
                Log.e("LoginScreen", "Login button clicked")
                navController.navigate("feedScreenNav/{$userID}") {
                    popUpTo("loginScreenNav") {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            } else {
                SnackBarFile(
                    coroutineScope,
                    snackbarHostState,
                    "Invalid user info",
                    "Short"
                )
                Log.e("LoginScreen", "Invalid user info")
            }
        }
        Log.e(
            "InvalidInfo",
            "$invalidUserInfo This is the value of invalidUserInfo"
        )
    }
}
