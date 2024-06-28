package com.android.burakgunduz.bitirmeprojesi.ui.screens.components

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.android.burakgunduz.bitirmeprojesi.ui.theme.colors.successButtonColor
import com.android.burakgunduz.bitirmeprojesi.ui.theme.fonts.archivoFonts

@Composable
fun MainButtonForAuth(action: () -> Unit, isDarkModeOn: Boolean, buttonText: String) {
    ElevatedButton(
        onClick = action,
        colors = successButtonColor(isDarkModeOn = isDarkModeOn),
        shape = AbsoluteRoundedCornerShape(7.dp),
        modifier = Modifier.size(350.dp, 50.dp)
    ) {
        Text(
            text = buttonText,
            style = MaterialTheme.typography.titleMedium,
            fontFamily = archivoFonts,
            fontWeight = FontWeight.ExtraBold,
        )
    }
}