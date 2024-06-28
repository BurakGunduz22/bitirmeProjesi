package com.android.burakgunduz.bitirmeprojesi.ui.screens.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.burakgunduz.bitirmeprojesi.ui.theme.colors.favoriteColorChanger
import com.android.burakgunduz.bitirmeprojesi.ui.theme.colors.itemSubTitleTextColorChanger
import com.android.burakgunduz.bitirmeprojesi.ui.theme.colors.itemTitleTextColorChanger
import com.android.burakgunduz.bitirmeprojesi.ui.theme.fonts.archivoFonts
import com.android.burakgunduz.bitirmeprojesi.ui.theme.fonts.robotoFonts


@Composable
fun ItemCardTitle(titleName: String, priceValue: String, isDarkModeOn: Boolean) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = titleName,
            fontSize = 20.sp,
            fontFamily = robotoFonts,
            fontWeight = FontWeight.Bold,
            color = itemTitleTextColorChanger(isDarkModeOn = isDarkModeOn),
            modifier = Modifier
                .padding(top = 50.dp, start = 10.dp)
                .width(250.dp),
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
        )
    }
}

@Composable
fun ItemCardSubTitletext(subTitleName: String, isDarkModeOn: Boolean, paddingValue: Int) {
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

@Composable
fun ItemConditions(itemCondition: Int, isDarkModeOn: Boolean) {
    return when (itemCondition) {
        0 -> Text(
            "New",
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 15.sp,
            fontFamily = robotoFonts,
            fontWeight = FontWeight.Medium,
            color = itemSubTitleTextColorChanger(isDarkModeOn = isDarkModeOn),
            modifier = Modifier.padding(bottom = 5.dp, start = 5.dp)
        )

        1 -> Text(
            "Used",
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 15.sp,
            fontFamily = robotoFonts,
            fontWeight = FontWeight.Medium,
            color = itemSubTitleTextColorChanger(isDarkModeOn = isDarkModeOn),
            modifier = Modifier.padding(bottom = 5.dp, start = 5.dp)
        )

        2 -> Text(
            "Refurbished",
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 15.sp,
            fontFamily = robotoFonts,
            fontWeight = FontWeight.Medium,
            color = itemSubTitleTextColorChanger(isDarkModeOn = isDarkModeOn),
            modifier = Modifier.padding(bottom = 5.dp, start = 5.dp)
        )

        else -> Text(
            "Unknown",
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 15.sp,
            fontFamily = robotoFonts,
            fontWeight = FontWeight.Medium,
            color = itemSubTitleTextColorChanger(isDarkModeOn = isDarkModeOn),
            modifier = Modifier.padding(bottom = 5.dp, start = 5.dp)
        )
    }
}

@Composable
fun PriceCard(priceValue: String, isDarkModeOn: Boolean) {

    Box(
        modifier = Modifier
            .size(80.dp, 50.dp)
            .padding(bottom = 5.dp, end = 5.dp)
            ,
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = priceValue,
                fontSize = 19.sp,
                fontFamily = archivoFonts,
                fontWeight = FontWeight.ExtraBold,
                color = itemTitleTextColorChanger(isDarkModeOn = isDarkModeOn),
                modifier = Modifier.width(50.dp),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )
            Text(
                text = " €",
                fontSize = 14.sp,
                fontFamily = robotoFonts,
                fontWeight = FontWeight.ExtraBold,
                color = itemTitleTextColorChanger(isDarkModeOn = isDarkModeOn),
                maxLines = 1,
            )
        }
    }
}