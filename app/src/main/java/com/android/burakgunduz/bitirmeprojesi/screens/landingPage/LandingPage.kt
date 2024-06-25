package com.android.burakgunduz.bitirmeprojesi.screens.landingPage

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.android.burakgunduz.bitirmeprojesi.screens.landingPage.landingPage.LandingScreen
import com.android.burakgunduz.bitirmeprojesi.screens.landingPage.loginScreen.LoginScreen
import com.android.burakgunduz.bitirmeprojesi.screens.landingPage.registerScreen.RegisterScreen
import com.android.burakgunduz.bitirmeprojesi.screens.landingPage.resetPasswordScreen.ResetPasswordScreen
import com.android.burakgunduz.bitirmeprojesi.viewModels.AuthViewModel
import kotlinx.coroutines.delay

@Composable
fun LandingPage(
    navController: NavController,
    isDarkModeOn: Boolean,
    iconSize: Int,
    authViewModel: AuthViewModel
) {
    val screenNumber = remember { mutableIntStateOf(0) }
    val cardExpanded = remember { mutableStateOf(false) }
    val isLoading = remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .offset(y = if (cardExpanded.value) -(iconSize.dp / 150) else iconSize.dp / 50),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
                .fillMaxHeight(if (cardExpanded.value) 0.8f else 0.7f),
            shape = AbsoluteRoundedCornerShape(30.dp),
        ) {
            if (isLoading.value) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                LaunchedEffect(isLoading.value) {
                    delay(500)
                    isLoading.value = false
                }
            } else {
                when (screenNumber.intValue) {
                    0 -> LandingScreen(
                        isDarkModeOn,
                        iconSize,
                        cardExpanded,
                        screenNumber,
                        isLoading
                    )

                    1 -> LoginScreen(
                        navController = navController,
                        isDarkModeOn = isDarkModeOn,
                        cardExpanded = cardExpanded,
                        iconSize = iconSize,
                        screenNumber = screenNumber,
                        authViewModel = authViewModel,
                        isLoading
                    )

                    2 -> RegisterScreen(
                        navController = navController,
                        isDarkModeOn = isDarkModeOn,
                        screenNumber = screenNumber,
                        authViewModel = authViewModel,
                        cardShrank = cardExpanded,
                        isLoading
                    )
                    3 -> ResetPasswordScreen(
                        isDarkModeOn = isDarkModeOn,
                        screenNumber = screenNumber,
                        authViewModel = authViewModel,
                        isLoading =isLoading
                    )
                }
            }
        }
    }
}
