package com.android.burakgunduz.bitirmeprojesi.ui.screens.landingPage.subScreens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
fun RegisterScreen(
    navController: NavController,
    isDarkModeOn: Boolean,
    screenNumber: MutableState<Int>,
    authViewModel: AuthViewModel,
    cardShrank: MutableState<Boolean>,
    isLoading: MutableState<Boolean>,
    snackbarHostState: SnackbarHostState
) {
    var nameInputString by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    var mailRegisterString by remember { mutableStateOf("") }
    var passwordRegisterString by remember { mutableStateOf("") }
    var confirmPasswordRegisterString by remember { mutableStateOf("") }
    var phoneNumberInputString by remember { mutableStateOf("") }
    val invalidRegister = remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

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
                    text = "Create An Account",
                    fontFamily = archivoFonts,
                    fontWeight = FontWeight.ExtraBold,
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Left,
                    modifier = Modifier.padding(start = 20.dp, top = 10.dp, bottom = 10.dp),
                    letterSpacing = (-1).sp,
                )
            }
        }
        TextFieldForAuth(
            takeAuthValue = nameInputString,
            labelText = "Name",
            focusManager = focusManager,
        ) {
            nameInputString = it
        }
        TextFieldForAuth(
            takeAuthValue = mailRegisterString,
            labelText = "Email",
            authKeyboardType("email"),
            focusManager = focusManager,

            ) {
            mailRegisterString = it
        }
        TextFieldForAuth(
            takeAuthValue = phoneNumberInputString,
            labelText = "Phone Number",
            authKeyboardType("phone"),
            focusManager = focusManager,

            ) {
            phoneNumberInputString = it
        }
        TextFieldForAuth(
            takeAuthValue = passwordRegisterString,
            labelText = "Password",
            authKeyboardType("password"),
            focusManager = focusManager,

            ) {
            passwordRegisterString = it
        }
        TextFieldForAuth(
            takeAuthValue = confirmPasswordRegisterString,
            labelText = "Confirm Password",
            authKeyboardType("password"),
            focusManager = focusManager,
            fieldCount = 5,
            errorText = if (passwordRegisterString != confirmPasswordRegisterString) "Passwords do not match" else ""
        ) {
            confirmPasswordRegisterString = it
        }
        Spacer(modifier = Modifier.padding(10.dp))
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
                    snackbarHostState,
                    coroutineScope
                )
            },
            isDarkModeOn = isDarkModeOn,
            buttonText = "CREATE AN ACCOUNT"
        )
        GoogleAuthButton(navController, isDarkModeOn, "Sign in with Google")
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "ALREADY AN HAVE ACCOUNT?",
                fontFamily = archivoFonts,
                fontWeight = FontWeight.ExtraBold,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
            )
            TextButton(onClick = {
                cardShrank.value = !cardShrank.value
                screenNumber.value = 1
                isLoading.value = true
            }) {
                Text(
                    text = "LOGIN",
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

fun isValidPassword(password: String): Boolean {
    val numberPattern = ".*\\d.*"
    val lowerCasePattern = ".*[a-z].*"
    val upperCasePattern = ".*[A-Z].*"
    val specialCharPattern = ".*[!@#$%^&*()-+=|<>?{}\\[\\]~.,].*"
    return password.length >= 8 &&
            password.matches(numberPattern.toRegex()) &&
            password.matches(lowerCasePattern.toRegex()) &&
            password.matches(upperCasePattern.toRegex()) &&
            password.matches(specialCharPattern.toRegex())
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
    authViewModel: AuthViewModel,
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope
) {
    if (mailRegisterString.isBlank() || passwordRegisterString.isBlank() || nameInputString.isBlank() || phoneNumberInputString.isBlank()) {
        SnackBarFile(coroutineScope, snackbarHostState, "Fields cannot be empty", "Short")
    }else if (passwordRegisterString != confirmPasswordRegisterString) {
        Log.e("Register Screen", "Passwords do not match")
        SnackBarFile(coroutineScope, snackbarHostState, "Passwords do not match", "Short")
    } else if (!isValidPassword(passwordRegisterString)) {
        SnackBarFile(
            coroutineScope,
            snackbarHostState,
            " Password must be at least 8 characters and include at least one uppercase letter, one lowercase letter, one number, and one special character.",
            "Short"
        )
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
                SnackBarFile(coroutineScope, snackbarHostState, "Email already in use", "Short")
                Log.e("Register Screen", "Register button clicked")
            }
        }
    }
}