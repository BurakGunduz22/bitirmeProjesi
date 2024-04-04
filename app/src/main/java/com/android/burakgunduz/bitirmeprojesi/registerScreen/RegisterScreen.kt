package com.android.burakgunduz.bitirmeprojesi.registerScreen

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.android.burakgunduz.bitirmeprojesi.TextFieldForAuth
import com.android.burakgunduz.bitirmeprojesi.landingPage.iconCardColor

@Composable
fun RegisterScreen(navController: NavController, isDarkModeOn: Boolean, iconSize: Int) {
    var nameInputString by remember { mutableStateOf("") }
    var mailRegisterString by remember { mutableStateOf("") }
    var passwordRegisterString by remember { mutableStateOf("") }
    var phoneNumberInputString by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Card(
            modifier = Modifier
                .fillMaxHeight(0.8f)
                .fillMaxWidth()
                .animateContentSize(),
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
                            imageVector = Icons.Filled.MonetizationOn,
                            contentDescription = "",
                            modifier = Modifier.size(iconSize.dp / 8)
                        )
                    }
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(bottom = iconSize.dp / 25)
                    ) {
                        Text(
                            text = "New Way of Shopping",
                            style = MaterialTheme.typography.headlineSmall,
                            textAlign = TextAlign.Left
                        )
                    }
                }
                TextFieldForAuth(takeAuthValue = nameInputString, labelText = "Name") {
                    nameInputString = it
                }
                TextFieldForAuth(takeAuthValue = mailRegisterString, labelText = "Email") {
                    mailRegisterString = it
                }
                TextFieldForAuth(takeAuthValue = passwordRegisterString, labelText = "Password") {
                    passwordRegisterString = it
                }
                TextFieldForAuth(
                    takeAuthValue = phoneNumberInputString,
                    labelText = "Phone Number"
                ) {
                    phoneNumberInputString = it
                }
                ElevatedButton(onClick = {
                    Log.e("Register Screen", "Register button clicked")
                    navController.navigate("loginScreenNav") {
                        popUpTo("registerScreenNav") {
                            inclusive = true
                        }
                    }
                }) {
                    Text(text = "Register")
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Already have an account?")
                    TextButton(onClick = { navController.navigate("loginScreenNav") }) {
                        Text(text = "Login", textDecoration = TextDecoration.Underline)
                    }
                }
            }
        }
    }
}