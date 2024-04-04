package com.android.burakgunduz.bitirmeprojesi.landingPage

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
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun LandingPage(navController: NavController, isDarkModeOn: Boolean, iconSize: Int) {
    var isNavigating by remember { mutableStateOf(true) }
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Card(
            modifier = Modifier
                .offset(y = iconSize.dp / 50)
                .fillMaxWidth()
                .fillMaxHeight(0.7f),
            shape = AbsoluteRoundedCornerShape(30.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(iconSize.dp / 50)
                ) {
                    Card(
                        shape = CircleShape,
                        modifier = Modifier
                            .size(iconSize.dp / 10)
                            .padding(iconSize.dp / 100),
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
                            style = MaterialTheme.typography.headlineLarge
                        )
                        Text(
                            text = "Turn clutter into cash. Effortless selling, endless possibilities.",
                            textAlign = TextAlign.Center,
                        )
                    }
                }
                ElevatedButton(onClick = { navController.navigate("registerScreenNav") }) {
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

@Composable
fun iconCardColor(isDarkModeOn: Boolean): CardColors {
    return if (!isDarkModeOn) {
        CardDefaults.cardColors(containerColor = Color.White)

    } else {
        CardDefaults.cardColors(containerColor = Color.Black)
    }
}