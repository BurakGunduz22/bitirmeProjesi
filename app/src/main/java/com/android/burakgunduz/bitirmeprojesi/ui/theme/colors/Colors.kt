package com.android.burakgunduz.bitirmeprojesi.ui.theme.colors

import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardColors
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.android.burakgunduz.bitirmeprojesi.ui.theme.SuccessButton
import com.android.burakgunduz.bitirmeprojesi.ui.theme.dark_SuccessButton
import com.android.burakgunduz.bitirmeprojesi.ui.theme.md_theme_dark_inversePrimary
import com.android.burakgunduz.bitirmeprojesi.ui.theme.md_theme_light_surfaceTint
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
fun editButtonColorChanger(isDarkModeOn: Boolean): IconButtonColors {
    return if (isDarkModeOn) {
        IconButtonDefaults.iconButtonColors(contentColor = Color(0xFFE2E1E1))
    } else {
        IconButtonDefaults.iconButtonColors(contentColor = Color(0xFFFFFFFF))
    }
}

@Composable
fun itemTitleTextColorChanger(isDarkModeOn: Boolean): Color {
    return if (isDarkModeOn) {
        Color(0xFFE2E1E1)
    } else {
        Color(0xFFFFFFFF)
    }
}

@Composable
fun itemSubTitleTextColorChanger(isDarkModeOn: Boolean): Color {
    return if (isDarkModeOn) {
        Color(0xFFCECDCD)
    } else {
        Color(0xFFFFFFFF)
    }
}

@Composable
fun itemPriceTextColorChanger(isDarkModeOn: Boolean): Color {
    return if (isDarkModeOn) {
        Color(0xFF4B4949)
    } else {
        Color.White
    }
}

val floatingLikeButtonContainer = Color(0xFFF64242)
val floatingLikeButtonContent = Color(0xFFFFFFFF)

@Composable
fun cardPriceColorChanger(isDarkModeOn: Boolean): CardColors {
    return if (isDarkModeOn) {
        CardColors(
            containerColor = md_theme_dark_inversePrimary,
            contentColor = Color.White,
            disabledContentColor = Color(0xFF4B4949),
            disabledContainerColor = Color(0xFF4B4949),
        )
    } else {
        CardColors(
            containerColor = md_theme_light_surfaceTint,
            contentColor = Color.White,
            disabledContentColor = Color(0xFF4B4949),
            disabledContainerColor = Color(0xFF4B4949),
        )
    }
}

@Composable
fun favoriteColorChanger(isDarkModeOn: Boolean): IconButtonColors {
    return if (isDarkModeOn) {
        IconButtonColors(
            containerColor = Color.Transparent,
            contentColor = Color.White,
            disabledContentColor = Color(0xFF4B4949),
            disabledContainerColor = Color(0xFF4B4949),
        )
    } else {
        IconButtonColors(
            containerColor = Color.Transparent,
            contentColor = Color.White,
            disabledContentColor = Color(0xFF4B4949),
            disabledContainerColor = Color(0xFF4B4949),
        )
    }
}

@Composable
fun googleColorChanger(isDarkModeOn: Boolean) : ButtonColors {
    return if (isDarkModeOn) {
        ButtonColors(
            containerColor = Color.Black,
            contentColor = Color.White,
            disabledContentColor = Color(0xFF4B4949),
            disabledContainerColor = Color(0xFF4B4949),
        )
    } else {
        ButtonColors(
            containerColor = Color.White,
            contentColor = Color.Black,
            disabledContentColor = Color(0xFF4B4949),
            disabledContainerColor = Color(0xFF4B4949),
        )
    }
}