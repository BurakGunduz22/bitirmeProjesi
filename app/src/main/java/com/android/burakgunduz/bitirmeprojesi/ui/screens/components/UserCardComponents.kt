package com.android.burakgunduz.bitirmeprojesi.ui.screens.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.burakgunduz.bitirmeprojesi.ui.theme.colors.cardPriceColorChanger
import com.android.burakgunduz.bitirmeprojesi.ui.theme.colors.editButtonColorChanger
import com.android.burakgunduz.bitirmeprojesi.ui.theme.colors.itemPriceTextColorChanger
import com.android.burakgunduz.bitirmeprojesi.ui.theme.colors.itemSubTitleTextColorChanger
import com.android.burakgunduz.bitirmeprojesi.ui.theme.colors.itemTitleTextColorChanger
import com.android.burakgunduz.bitirmeprojesi.ui.theme.fonts.archivoFonts
import com.android.burakgunduz.bitirmeprojesi.ui.theme.fonts.robotoFonts


@Composable
fun UserCardTitle(titleName: String, isDarkModeOn: Boolean) {
    Text(
        text = titleName,
        fontSize = 20.sp,
        fontFamily = robotoFonts,
        fontWeight = FontWeight.Bold,
        color = itemTitleTextColorChanger(isDarkModeOn = isDarkModeOn),
        modifier = Modifier.padding(top = 60.dp, start = 10.dp)
    )
}

@Composable
fun CountText(
    subTitleName: String,
    isDarkModeOn: Boolean,
    paddingValue: Int,
    countIcon: ImageVector
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        Icon(
            imageVector = countIcon,
            contentDescription = "Count",
            tint = itemSubTitleTextColorChanger(isDarkModeOn = isDarkModeOn),
            modifier = Modifier
                .size(30.dp)
                .padding(bottom = 5.dp)
        )
        Text(
            text = subTitleName,
            fontSize = 20.sp,
            fontFamily = robotoFonts,
            fontWeight = FontWeight.Medium,
            color = itemSubTitleTextColorChanger(isDarkModeOn = isDarkModeOn),
            modifier = Modifier
                .padding(bottom = 5.dp, start = paddingValue.dp)
        )
    }
}

@Composable
fun PriceCard(priceValue: String, isDarkModeOn: Boolean, paddingValue: Int) {
    OutlinedCard(
        modifier = Modifier
            .size(80.dp, 45.dp)
            .padding(start = 10.dp),
        colors = cardPriceColorChanger(isDarkModeOn = isDarkModeOn),
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "$priceValue €",
                fontSize = 20.sp,
                fontFamily = archivoFonts,
                fontWeight = FontWeight.Medium,
                color = itemPriceTextColorChanger(isDarkModeOn = isDarkModeOn),
                modifier = Modifier.padding(top = paddingValue.dp)
            )
        }
    }
}

@Composable
fun EditButton(
    onClick: () -> Unit,
    isDarkModeOn: Boolean
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(60.dp),
        colors = editButtonColorChanger(isDarkModeOn = isDarkModeOn)
    ) {
        Icon(imageVector = Icons.Outlined.Edit, contentDescription = "Edit")
    }
}