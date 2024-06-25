package com.android.burakgunduz.bitirmeprojesi.screens.landingPage.loginScreen

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.android.burakgunduz.bitirmeprojesi.MainButtonForAuth
import com.android.burakgunduz.bitirmeprojesi.TextFieldForAuth
import com.android.burakgunduz.bitirmeprojesi.authKeyboardType
import com.android.burakgunduz.bitirmeprojesi.screens.landingPage.components.GoogleAuthButton
import com.android.burakgunduz.bitirmeprojesi.screens.landingPage.landingPage.iconCardColor
import com.android.burakgunduz.bitirmeprojesi.ui.theme.fonts.archivoFonts
import com.android.burakgunduz.bitirmeprojesi.viewModels.AuthViewModel


@Composable
fun LoginScreen(
    navController: NavController,
    isDarkModeOn: Boolean,
    cardExpanded: MutableState<Boolean>,
    iconSize: Int,
    screenNumber: MutableState<Int>,
    authViewModel: AuthViewModel,
    isLoading: MutableState<Boolean>
) {
    var mailInputString by remember { mutableStateOf("") }
    var passwordInputString by remember { mutableStateOf("") }
    val invalidUserInfo = remember { mutableStateOf(false) }
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
                    .size(iconSize.dp / 10),
                colors = iconCardColor(isDarkModeOn)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = "",
                    modifier = Modifier.size(iconSize.dp / 8)
                )
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
                    modifier = Modifier.padding(start = 20.dp, top = 10.dp, bottom = 5.dp)
                )
            }
        }
        TextFieldForAuth(
            takeAuthValue = mailInputString,
            labelText = "Email",
            authKeyboardType("email")
        ) {
            mailInputString = it
        }
        TextFieldForAuth(
            takeAuthValue = passwordInputString,
            labelText = "Password",
            authKeyboardType("password"),
            fieldSpace = 30,
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
                    authViewModel
                )
            },
            isDarkModeOn = isDarkModeOn,
            buttonText = "LOGIN"
        )
        GoogleAuthButton(navController, isDarkModeOn,"Login with Google")
        TextButton(onClick = {
            screenNumber.value = 3
            isLoading.value = true
        }) {
            Text(text = "Forgot Password?")
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Don't you have an account?")
            TextButton(onClick = {
                screenNumber.value = 2
                cardExpanded.value = !cardExpanded.value
                isLoading.value = true
            }) {
                Text(text = "Sign Up", textDecoration = TextDecoration.Underline)
            }
        }
    }
}

fun login(
    mailInputString: String,
    passwordInputString: String,
    navController: NavController,
    invalidUserInfo: Boolean,
    authViewModel: AuthViewModel
) {
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
            Log.e("LoginScreen", "Invalid user info")
        }
    }
    Log.e(
        "InvalidInfo",
        "$invalidUserInfo This is the value of invalidUserInfo"
    )
}
