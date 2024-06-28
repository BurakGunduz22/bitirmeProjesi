package com.android.burakgunduz.bitirmeprojesi.screens.landingPage.resetPasswordScreen

import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import com.android.burakgunduz.bitirmeprojesi.MainButtonForAuth
import com.android.burakgunduz.bitirmeprojesi.SnackBarFile
import com.android.burakgunduz.bitirmeprojesi.TextFieldForAuth
import com.android.burakgunduz.bitirmeprojesi.authKeyboardType
import com.android.burakgunduz.bitirmeprojesi.screens.landingPage.landingPage.iconCardColor
import com.android.burakgunduz.bitirmeprojesi.ui.theme.fonts.archivoFonts
import com.android.burakgunduz.bitirmeprojesi.viewModels.AuthViewModel
import kotlinx.coroutines.CoroutineScope


@Composable
fun ResetPasswordScreen(
    isDarkModeOn: Boolean,
    screenNumber: MutableState<Int>,
    authViewModel: AuthViewModel,
    isLoading: MutableState<Boolean>,
    snackbarHostState: SnackbarHostState,
) {
    var mailInputString by remember { mutableStateOf("") }
    val invalidUserInfo = remember { mutableStateOf(false) }
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    val callback = remember {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                screenNumber.value = 1
                isLoading.value = true
            }
        }
    }
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    DisposableEffect(backDispatcher) {
        backDispatcher?.addCallback(callback)
        onDispose {
            callback.remove()
        }
    }
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Card(
            shape = CircleShape,
            modifier = Modifier
                .size(100.dp)
                .border(2.dp, Color.Gray, CircleShape), // Add this line to create a border
            colors = iconCardColor(isDarkModeOn)
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Icon(
                    imageVector = Icons.Outlined.Lock,
                    contentDescription = "",
                    modifier = Modifier
                        .size(90.dp)
                        .padding(3.dp),
                )
            }
        }
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
                    text = "Reset Password",
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
            authKeyboardType("email"),
            focusManager = focusManager,
            fieldCount = 0,
        ) {
            mailInputString = it
        }
        MainButtonForAuth(
            action = {
                resetPassword(
                    mailInputString,
                    invalidUserInfo.value,
                    authViewModel,
                    snackbarHostState,
                    coroutineScope,
                    isLoading,
                    screenNumber
                )
            },
            isDarkModeOn = isDarkModeOn,
            buttonText = "SEND MAIL"
        )
        TextButton(
            onClick = {
                screenNumber.value = 1
                isLoading.value = true
            },
            modifier = Modifier.padding(top = 25.dp)
        ) {
            Text(
                text = "Go Back to Login",
                textDecoration = TextDecoration.Underline,
                fontFamily = archivoFonts,
                fontWeight = FontWeight.ExtraBold,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                )
        }
    }
}

fun resetPassword(
    mailInputString: String,
    invalidUserInfo: Boolean,
    authViewModel: AuthViewModel,
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope,
    isLoading: MutableState<Boolean>,
    screenNumber: MutableState<Int>
) {
    if (mailInputString.isBlank()) {
        SnackBarFile(coroutineScope, snackbarHostState, "Email field cannot be empty", "Short")
    } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(mailInputString).matches()) {
        SnackBarFile(coroutineScope, snackbarHostState, "Invalid email address", "Short")
    } else {
        authViewModel.resetPassword(
            mailInputString
        )
        screenNumber.value = 1
        isLoading.value = true
        Log.e(
            "InvalidInfo",
            "$invalidUserInfo This is the value of invalidUserInfo"
        )
    }
}
