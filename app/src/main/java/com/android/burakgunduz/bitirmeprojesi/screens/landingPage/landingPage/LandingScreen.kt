package com.android.burakgunduz.bitirmeprojesi.screens.landingPage.landingPage

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.burakgunduz.bitirmeprojesi.MainButtonForAuth
import com.android.burakgunduz.bitirmeprojesi.R
import com.android.burakgunduz.bitirmeprojesi.ui.theme.colors.titleTextColorChanger
import com.android.burakgunduz.bitirmeprojesi.ui.theme.fonts.archivoFonts

@Composable
fun LandingScreen(
    isDarkModeOn: Boolean,
    iconSize: Int,
    cardExpanded: MutableState<Boolean>,
    screenNumber: MutableState<Int>,
    isLoading: MutableState<Boolean>
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
            val image = if (isDarkModeOn) {
                painterResource(id = R.drawable.saflogo)
            } else {
                painterResource(id = R.drawable.saflogo_light)
            }
            Image(
                painter = image,
                contentDescription = "icon",
                modifier = Modifier
                    .size(200.dp)
                    .padding(bottom = 25.dp)
            )

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
                    color = titleTextColorChanger(isDarkModeOn),
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
        MainButtonForAuth(action = {
            screenNumber.value = 2
            cardExpanded.value = !cardExpanded.value
            isLoading.value = true
        }, isDarkModeOn = isDarkModeOn, buttonText = "CREATE AN ACCOUNT")
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "ALREADY HAVE AN ACCOUNT?",
                fontFamily = archivoFonts,
                fontWeight = FontWeight.ExtraBold,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
            )
            TextButton(onClick = {
                screenNumber.value = 1
                isLoading.value = true
            }) {
                Text(text = "LOGIN",
                    textDecoration = TextDecoration.Underline,
                    fontFamily = archivoFonts,
                    fontWeight = FontWeight.ExtraBold,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,)
            }
        }
    }

}

@Composable
fun iconCardColor(isDarkModeOn: Boolean): CardColors {
    return if (!isDarkModeOn) {
        CardDefaults.cardColors(containerColor = Color.Transparent)

    } else {
        CardDefaults.cardColors(containerColor = Color.Transparent)
    }
}