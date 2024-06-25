package com.android.burakgunduz.bitirmeprojesi

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.android.burakgunduz.bitirmeprojesi.ui.theme.fonts.archivoFonts

@Composable
fun FakeTopBar(navController: NavController, screenName: String) {
    Column {
        IconButton(onClick = {
            navController.navigateUp()
        }) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = "Back"
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = screenName,
                fontFamily = archivoFonts,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 34.sp,
                letterSpacing = (-2).sp,
                style = MaterialTheme.typography.titleLarge,
            )
        }
    }
}