package com.android.burakgunduz.bitirmeprojesi.screens.userProfileScreen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.android.burakgunduz.bitirmeprojesi.viewModels.ItemCard


@Composable
fun UserListedItemCard(
    itemCardValue: ItemCard,
    imageUrl: String,
    isDarkModeOn: Boolean,
    navController: NavController,
) {
    val colorStops = arrayOf(
        0.0f to Color.hsl(0f, 0f, 0f, 0f),
        0.1f to Color.hsl(0f, 0f, 0.1f, 0.1f),
        0.4f to Color.hsl(0f, 0f, 0.1f, 0.3f),
        1f to Color.Black
    )
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth(0.95f)
            .size(200.dp, 250.dp)
            .padding(10.dp)
            .clickable { navController.navigate("itemDetailsPageNav/${itemCardValue.itemID}") }

    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (imageUrl.isNotEmpty()) {
                SubcomposeAsyncImage(
                    model = imageUrl,
                    contentDescription = "Item Image",
                    contentScale = ContentScale.FillWidth,
                    alignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    val state = painter.state
                    when (state) {
                        is AsyncImagePainter.State.Loading -> {
                            CircularProgressIndicator()
                        }

                        is AsyncImagePainter.State.Empty, is AsyncImagePainter.State.Error -> {
                            Icon(imageVector = Icons.Outlined.Error, contentDescription = "Person")
                        }

                        else -> {
                            SubcomposeAsyncImageContent()
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.horizontalGradient(colorStops = colorStops))
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.Bottom
                ) {

                    val itemViewCount = itemCardValue.viewCount.toString()
                    val itemLikeCount = itemCardValue.likeCount.toString()
                    val messageCount = 0
                    TitleText(
                        titleName = itemCardValue.itemName,
                        isDarkModeOn = isDarkModeOn,
                    )
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.End
                    ) {
                        EditButton(
                            onClick = { navController.navigate("editItemScreenNav/${itemCardValue.itemID}") },
                            isDarkModeOn = isDarkModeOn
                        )
                        CountText(itemViewCount, isDarkModeOn, 5, Icons.Filled.BarChart)
                        Spacer(modifier = Modifier.height(10.dp))
                        CountText(itemLikeCount, isDarkModeOn, 5, Icons.Filled.Favorite)
                        Spacer(modifier = Modifier.height(10.dp))
                        CountText(
                            messageCount.toString(),
                            isDarkModeOn,
                            5,
                            Icons.AutoMirrored.Filled.Message
                        )
                    }


                }


            }
        }
    }
}

