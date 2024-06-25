package com.android.burakgunduz.bitirmeprojesi.screens.landingPage.resetPasswordScreen

import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.android.burakgunduz.bitirmeprojesi.MainButtonForAuth
import com.android.burakgunduz.bitirmeprojesi.TextFieldForAuth
import com.android.burakgunduz.bitirmeprojesi.authKeyboardType
import com.android.burakgunduz.bitirmeprojesi.ui.theme.fonts.archivoFonts
import com.android.burakgunduz.bitirmeprojesi.viewModels.AuthViewModel


@Composable
fun ResetPasswordScreen(
    isDarkModeOn: Boolean,
    screenNumber: MutableState<Int>,
    authViewModel: AuthViewModel,
    isLoading: MutableState<Boolean>
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
            authKeyboardType("email")
        ) {
            mailInputString = it
        }
        MainButtonForAuth(
            action = {
                resetPassword(
                    mailInputString,
                    invalidUserInfo.value,
                    authViewModel
                )
                screenNumber.value = 1
                isLoading.value = true
            },
            isDarkModeOn = isDarkModeOn,
            buttonText = "Send Mail"
        )
    }
}

fun resetPassword(
    mailInputString: String,
    invalidUserInfo: Boolean,
    authViewModel: AuthViewModel,
) {
    authViewModel.resetPassword(
        mailInputString
    )
    Log.e(
        "InvalidInfo",
        "$invalidUserInfo This is the value of invalidUserInfo"
    )
}
