package com.android.burakgunduz.bitirmeprojesi.screens.loginScreen

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import com.android.burakgunduz.bitirmeprojesi.firebaseAuths.getAuth
import com.android.burakgunduz.bitirmeprojesi.ui.theme.fonts.archivoFonts
import com.android.burakgunduz.bitirmeprojesi.screens.landingPage.iconCardColor
import com.google.firebase.auth.FirebaseAuth


@Composable
fun LoginScreen(
    navController: NavController,
    isDarkModeOn: Boolean,
    iconSize: Int,
    authParam: FirebaseAuth
) {
    var cardExpanded by remember { mutableStateOf(false) }
    var mailInputString by remember { mutableStateOf("") }
    var passwordInputString by remember { mutableStateOf("") }
    val invalidUserInfo = remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Card(
            modifier = Modifier
                .animateContentSize()
                .offset(y = if (cardExpanded) -(iconSize.dp / 150) else iconSize.dp / 50)
                .fillMaxHeight(if (cardExpanded) 0.8f else 0.7f)
                .fillMaxWidth(),
            shape = AbsoluteRoundedCornerShape(30.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
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
                            modifier = Modifier.padding(start = 20.dp, top = 10.dp, bottom = 10.dp)
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
                            authParam,
                            navController,
                            invalidUserInfo.value
                        )
                    },
                    isDarkModeOn = isDarkModeOn,
                    buttonText = "LOGIN"
                )
                TextButton(onClick = { /*TODO*/ }) {
                    Text(text = "Forgot Password?")
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Don't you have an account?")
                    TextButton(onClick = {
                        navController.navigate("registerScreenNav")
                        cardExpanded = !cardExpanded
                    }) {
                        Text(text = "Sign Up", textDecoration = TextDecoration.Underline)
                    }
                }
            }
        }
    }
}

fun login(
    mailInputString: String,
    passwordInputString: String,
    authParam: FirebaseAuth,
    navController: NavController,
    invalidUserInfo: Boolean
) {

    getAuth(
        mailInputString,
        passwordInputString,
        authParam,
    ) {
        if (!it) {
            Log.e("LoginScreen", "Login button clicked")
            navController.navigate("feedScreenNav/1") {
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
