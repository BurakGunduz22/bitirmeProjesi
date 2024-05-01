package com.android.burakgunduz.bitirmeprojesi.colors

import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.android.burakgunduz.bitirmeprojesi.ui.theme.SuccessButton
import com.android.burakgunduz.bitirmeprojesi.ui.theme.dark_SuccessButton
import com.android.burakgunduz.bitirmeprojesi.ui.theme.titleTextColor
import com.android.burakgunduz.bitirmeprojesi.ui.theme.titleTextColorDarkMode


@Composable
fun successButtonColor(isDarkModeOn: Boolean): ButtonColors {
    return if (isDarkModeOn) {
        ButtonDefaults.buttonColors(containerColor = dark_SuccessButton)
    } else {
        ButtonDefaults.buttonColors(containerColor = SuccessButton)
    }
}

@Composable
fun titleTextColorChanger(isDarkModeOn: Boolean): Color {
    return if (isDarkModeOn) {
        titleTextColorDarkMode
    } else {
        titleTextColor
    }
}

@Composable
fun itemTitleTextColorChanger(isDarkModeOn: Boolean): Color {
    return if (isDarkModeOn) {
        Color.Gray
    } else {
        Color.White
    }
}
@Composable
fun itemSubTitleTextColorChanger(isDarkModeOn: Boolean): Color {
    return if (isDarkModeOn) {
        Color(0xFF4B4949)
    } else {
        Color(0xFFA5A5A5)
    }
}