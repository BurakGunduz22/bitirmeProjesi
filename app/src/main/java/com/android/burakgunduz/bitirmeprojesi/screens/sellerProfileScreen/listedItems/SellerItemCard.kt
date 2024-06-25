package com.android.burakgunduz.bitirmeprojesi.screens.sellerProfileScreen.listedItems

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import com.android.burakgunduz.bitirmeprojesi.ui.theme.colors.itemSubTitleTextColorChanger
import com.android.burakgunduz.bitirmeprojesi.ui.theme.colors.itemTitleTextColorChanger
import com.android.burakgunduz.bitirmeprojesi.ui.theme.fonts.robotoFonts
import com.android.burakgunduz.bitirmeprojesi.viewModels.ItemCard


@Composable
fun SellerItemCard(
    itemCardValue: ItemCard,
    imageUrl: String,
    isDarkModeOn: Boolean,
    navController: NavController
) {
    val colorStops = arrayOf(
        0.0f to Color.hsl(0f, 0f, 0f, 0f),
        0.4f to Color.hsl(0f, 0f, 0.1f, 0.1f),
        1f to Color.Black
    )
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth(0.95f)
            .size(200.dp, 250.dp)
            .padding(10.dp)
            .clickable { navController.navigate("itemDetailsPageNav/${itemCardValue.itemID}") }
    ) {
        Box {
            if (imageUrl.isNotEmpty()) {
                SubcomposeAsyncImage(
                    model = imageUrl,
                    contentDescription = "Item Image",
                    contentScale = ContentScale.FillWidth
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.verticalGradient(colorStops = colorStops))
                )
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.Start
                ) {
                    TitleText(itemCardValue.itemName, isDarkModeOn)
                    Row {
                        SubTitletext(itemCardValue.itemCategory, isDarkModeOn, 10)
                        SubTitletext("•", isDarkModeOn, 5)
                        SubTitletext(itemCardValue.itemTown, isDarkModeOn, 5)
                    }
                }
            }
        }
    }
}

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
        modifier = Modifier.padding(bottom = 25.dp, start = paddingValue.dp)
    )
}

