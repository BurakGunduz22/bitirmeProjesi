package com.android.burakgunduz.bitirmeprojesi.screens.itemDetailsPage.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.burakgunduz.bitirmeprojesi.ui.theme.fonts.archivoFonts


@Composable
fun ItemConditions(itemCondition: Int) {
    return when (itemCondition) {
        0 -> Text(
            "New",
            style = MaterialTheme.typography.bodyMedium,
            fontFamily = archivoFonts,
            fontWeight = FontWeight.SemiBold,
            color = Color.Green,
            modifier = Modifier.padding(10.dp)
        )

        1 -> Text(
            "Used",
            style = MaterialTheme.typography.bodyMedium,
            fontFamily = archivoFonts,
            fontWeight = FontWeight.SemiBold,
            color = Color.Yellow,
            modifier = Modifier.padding(10.dp)
        )

        2 -> Text(
            "Refurbished",
            style = MaterialTheme.typography.bodyMedium,
            fontFamily = archivoFonts,
            fontWeight = FontWeight.SemiBold,
            color = Color.Yellow,
            modifier = Modifier.padding(10.dp)
        )

        else -> Text(
            "Unknown", style = MaterialTheme.typography.bodyMedium, fontFamily = archivoFonts,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
fun ItemDescription(itemDesc: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Description",
            fontFamily = archivoFonts,
            letterSpacing = (-2).sp,
            fontWeight = FontWeight.ExtraBold,
            style = MaterialTheme.typography.titleLarge,
        )
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, end = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = itemDesc,
            fontFamily = archivoFonts,
            fontWeight = FontWeight.Normal,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
fun ItemPrice(itemPrice: Int) {
    Row(
        modifier = Modifier
            .padding(10.dp, end = 25.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$itemPrice €",
            fontFamily = archivoFonts,
            fontWeight = FontWeight.ExtraBold,
            style = MaterialTheme.typography.titleLarge,
        )
    }
}
@Composable
fun ItemCategory(itemCategory: String,itemSubCategory: String) {
    Row(
        modifier = Modifier
            .padding(start = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = itemCategory,
            fontFamily = archivoFonts,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = (-2).sp,
            style = MaterialTheme.typography.titleMedium,
            textDecoration = TextDecoration.Underline
        )
        Text(
            text = ">",
            fontFamily = archivoFonts,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = (-2).sp,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .padding(start = 5.dp, end = 5.dp, top = 2.dp)
        )
        Text(
            text = itemSubCategory,
            fontFamily = archivoFonts,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = (-2).sp,
            style = MaterialTheme.typography.titleMedium,
            textDecoration = TextDecoration.Underline,
        )
    }
}

@Composable
fun ItemName(itemName: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, top = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = itemName,
            fontFamily = archivoFonts,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = (-2).sp,
            style = MaterialTheme.typography.headlineLarge,
        )
    }
}
