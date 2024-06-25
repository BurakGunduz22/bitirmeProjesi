package com.android.burakgunduz.bitirmeprojesi.screens.landingPage.registerScreen

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.android.burakgunduz.bitirmeprojesi.ui.theme.fonts.archivoFonts
import com.android.burakgunduz.bitirmeprojesi.viewModels.AuthViewModel

@Composable
fun RegisterScreen(
    navController: NavController,
    isDarkModeOn: Boolean,
    screenNumber: MutableState<Int>,
    authViewModel: AuthViewModel,
    cardShrank: MutableState<Boolean>,
    isLoading: MutableState<Boolean>
) {
    var nameInputString by remember { mutableStateOf("") }
    var mailRegisterString by remember { mutableStateOf("") }
    var passwordRegisterString by remember { mutableStateOf("") }
    var confirmPasswordRegisterString by remember { mutableStateOf("") }
    var phoneNumberInputString by remember { mutableStateOf("") }
    val invalidRegister = remember { mutableStateOf(false) }
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
                    screenNumber,
                    cardShrank,
                    isLoading,
                    authViewModel,
                )
            },
            isDarkModeOn = isDarkModeOn,
            buttonText = "CREATE AN ACCOUNT"
        )
        GoogleAuthButton(navController,isDarkModeOn,"Sign in with Google")
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Already have an account?")
            TextButton(onClick = {
                cardShrank.value = !cardShrank.value
                screenNumber.value = 1
                isLoading.value = true
            }) {
                Text(text = "Login", textDecoration = TextDecoration.Underline)
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
    screenNumber: MutableState<Int>,
    cardShrank: MutableState<Boolean>,
    isLoading: MutableState<Boolean>,
    authViewModel: AuthViewModel
) {
    if (passwordRegisterString != confirmPasswordRegisterString) {
        Log.e("Register Screen", "Passwords do not match")

    } else {
        authViewModel.addUserToDatabase(
            mailRegisterString,
            passwordRegisterString,
            nameInputString,
            phoneNumberInputString,
        ) { isComplete, _ ->
            if (!isComplete) {
                Log.e("Register Screen", "Register button clicked")
                screenNumber.value = 1
                cardShrank.value = !cardShrank.value
                screenNumber.value = 1
                isLoading.value = true
            } else {
                Log.e("Register Screen", "Register button clicked")
            }
        }
        authViewModel.sendConfirmation(mailRegisterString)
    }
}