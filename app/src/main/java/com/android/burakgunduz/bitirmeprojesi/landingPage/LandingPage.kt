package com.android.burakgunduz.bitirmeprojesi.landingPage

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
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.android.burakgunduz.bitirmeprojesi.colors.successButtonColor
import com.android.burakgunduz.bitirmeprojesi.colors.titleTextColorChanger
import com.android.burakgunduz.bitirmeprojesi.fonts.archivoFonts

@Composable
fun LandingPage(navController: NavController, isDarkModeOn: Boolean, iconSize: Int) {
    var cardExpanded by remember { mutableStateOf(false) }
    var isNavigating by remember { mutableStateOf(true) }
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
                .offset(y = if (cardExpanded) -(iconSize.dp / 150) else iconSize.dp / 50)
                .fillMaxHeight(if (cardExpanded) 0.8f else 0.7f),
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
                            style = MaterialTheme.typography.headlineLarge,
                            fontFamily = archivoFonts,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = -(2.sp),
                            color = titleTextColorChanger(isDarkModeOn)
                        )
                        Text(
                            text = "Turn clutter into cash. Effortless selling, endless possibilities.",
                            style = MaterialTheme.typography.headlineSmall,
                            textAlign = TextAlign.Center,
                            fontFamily = archivoFonts,
                            fontWeight = FontWeight.Thin,
                            letterSpacing = -(1.sp),
                        )
                    }
                }
                Button(
                    onClick = {
                        navController.navigate("registerScreenNav") {
                            popUpTo("landingScreenNav") {
                                inclusive = true
                            }
                        }
                        cardExpanded = !cardExpanded
                    },
                    colors = successButtonColor(isDarkModeOn = isDarkModeOn),
                    shape = AbsoluteRoundedCornerShape(7.dp),
                    modifier = Modifier.size(350.dp, 50.dp),
                ) {
                    Text(
                        text = "CREATE AN ACCOUNT",
                        style = MaterialTheme.typography.titleMedium,
                        fontFamily = archivoFonts,
                        fontWeight = FontWeight.ExtraBold,
                    )
                }
                Button(onClick = { navController.navigate("feedScreenNav/1") }) {
                    Text(text = "FeedScreen")
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Already have an account?")
                    TextButton(onClick = {
                        navController.navigate("loginScreenNav")
                    }) {
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