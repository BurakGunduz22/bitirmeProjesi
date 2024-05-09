package com.android.burakgunduz.bitirmeprojesi.screens.registerScreen

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.material3.Card
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
import com.android.burakgunduz.bitirmeprojesi.firebaseAuths.addUserToDatabase
import com.android.burakgunduz.bitirmeprojesi.ui.theme.fonts.archivoFonts
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun RegisterScreen(
    navController: NavController,
    isDarkModeOn: Boolean,
    iconSize: Int,
    authParam: FirebaseAuth,
    db: FirebaseFirestore
) {
    var cardShrank by remember { mutableStateOf(false) }
    var nameInputString by remember { mutableStateOf("") }
    var mailRegisterString by remember { mutableStateOf("") }
    var passwordRegisterString by remember { mutableStateOf("") }
    var confirmPasswordRegisterString by remember { mutableStateOf("") }
    var phoneNumberInputString by remember { mutableStateOf("") }
    val invalidRegister = remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Card(
            modifier = Modifier
                .animateContentSize()
                .padding(bottom =  10.dp)
                .fillMaxHeight(if (cardShrank) 0.7f else 0.8f)
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
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Create an Account",
                            fontFamily = archivoFonts,
                            fontWeight = FontWeight.ExtraBold,
                            style = MaterialTheme.typography.headlineSmall,
                            textAlign = TextAlign.Left,
                            modifier = Modifier.padding(start = 20.dp, top = 10.dp, bottom = 10.dp)
                        )
                    }
                }
                TextFieldForAuth(
                    takeAuthValue = nameInputString,
                    labelText = "Name"
                ) {
                    nameInputString = it
                }
                TextFieldForAuth(
                    takeAuthValue = mailRegisterString,
                    labelText = "Email",
                    authKeyboardType("email")
                ) {
                    mailRegisterString = it
                }
                TextFieldForAuth(
                    takeAuthValue = phoneNumberInputString,
                    labelText = "Phone Number",
                    authKeyboardType("phone")
                ) {
                    phoneNumberInputString = it
                }
                TextFieldForAuth(
                    takeAuthValue = passwordRegisterString,
                    labelText = "Password",
                    authKeyboardType("password")
                ) {
                    passwordRegisterString = it
                }
                TextFieldForAuth(
                    takeAuthValue = confirmPasswordRegisterString,
                    labelText = "Confirm Password",
                    authKeyboardType("password"),
                    fieldSpace = 30,
                ) {
                    confirmPasswordRegisterString = it
                }

                MainButtonForAuth(
                    action = {
                        registerFunc(
                            passwordRegisterString,
                            confirmPasswordRegisterString,
                            mailRegisterString,
                            nameInputString,
                            phoneNumberInputString,
                            authParam,
                            db,
                            navController,
                            invalidRegister.value
                        )
                    },
                    isDarkModeOn = isDarkModeOn,
                    buttonText = "CREATE AN ACCOUNT"
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Already have an account?")
                    TextButton(onClick = {
                        navController.navigate("loginScreenNav")
                        cardShrank = !cardShrank
                    }) {
                        Text(text = "Login", textDecoration = TextDecoration.Underline)
                    }
                }
            }
        }
    }
}

fun registerFunc(
    passwordRegisterString: String,
    confirmPasswordRegisterString: String,
    mailRegisterString: String,
    nameInputString: String,
    phoneNumberInputString: String,
    authParam: FirebaseAuth,
    db: FirebaseFirestore,
    navController: NavController,
    invalidRegister: Boolean
) {
    if (passwordRegisterString != confirmPasswordRegisterString) {
        Log.e("Register Screen", "Passwords do not match")

    } else {
        addUserToDatabase(
            mailRegisterString,
            passwordRegisterString,
            nameInputString,
            phoneNumberInputString,
            authParam,
            db
        ) {
            if (!it) {
                Log.e("Register Screen", "Register button clicked")
                navController.navigate("feedScreenNav/1") {
                    popUpTo("registerScreenNav") {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            } else {
                Log.e("Register Screen", "Register button clicked")
            }
        }

    }
}