package com.android.burakgunduz.bitirmeprojesi.screens.feedScreen.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.burakgunduz.bitirmeprojesi.ui.theme.colors.cardPriceColorChanger
import com.android.burakgunduz.bitirmeprojesi.ui.theme.colors.favoriteColorChanger
import com.android.burakgunduz.bitirmeprojesi.ui.theme.colors.itemPriceTextColorChanger
import com.android.burakgunduz.bitirmeprojesi.ui.theme.colors.itemSubTitleTextColorChanger
import com.android.burakgunduz.bitirmeprojesi.ui.theme.colors.itemTitleTextColorChanger
import com.android.burakgunduz.bitirmeprojesi.ui.theme.fonts.archivoFonts
import com.android.burakgunduz.bitirmeprojesi.ui.theme.fonts.robotoFonts


@Composable
fun TitleText(titleName: String, isDarkModeOn: Boolean) {
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
fun SubTitletext(subTitleName: String, isDarkModeOn: Boolean, paddingValue: Int) {
    Text(
        text = subTitleName,
        fontSize = 15.sp,
        fontFamily = robotoFonts,
        fontWeight = FontWeight.Medium,
        color = itemSubTitleTextColorChanger(isDarkModeOn = isDarkModeOn),
        modifier = Modifier.padding(bottom = 5.dp, start = paddingValue.dp)
    )
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
fun ItemLikeButton(
    isDarkModeOn: Boolean,
    toggleButtonChecked: MutableState<Boolean>,
    likeItem: () -> Unit,
    unLikeItem: () -> Unit
) {
    IconButton(
        onClick = {
            toggleButtonChecked.value = !toggleButtonChecked.value
            if (!toggleButtonChecked.value) unLikeItem()
            else likeItem()
        },
        modifier = Modifier.size(60.dp),
        colors = favoriteColorChanger(isDarkModeOn = isDarkModeOn)
    ) {
        Icon(
            imageVector = if (!toggleButtonChecked.value) Icons.Outlined.FavoriteBorder else Icons.Filled.Favorite,
            contentDescription = "Like Button",
            modifier = Modifier.size(30.dp)
        )
    }
}