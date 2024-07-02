package com.android.burakgunduz.bitirmeprojesi.ui.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.android.burakgunduz.bitirmeprojesi.viewModels.ItemCard
import com.android.burakgunduz.bitirmeprojesi.viewModels.ItemViewModel


@Composable
fun UserListedItemCard(
    itemCardValue: ItemCard,
    itemViewModel: ItemViewModel,
    imageUrl: String,
    isDarkModeOn: Boolean,
    navController: NavController,
    userID: String,
    itemsLoaded: MutableState<Boolean>
) {
    val colorStops = arrayOf(
        0.0f to Color.hsl(0f, 0f, 0f, 0f),
        0.4f to Color.hsl(0f, 0f, 0.1f, 0.1f),
        1f to Color.Black
    )

    // Observe the loading state
    val isDeleting by itemViewModel.isDeleting.collectAsState()

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth(0.95f)
            .size(200.dp, 150.dp)
            .padding(10.dp)
            .clip(shape = AbsoluteRoundedCornerShape(10))
            .clickable { navController.navigate("itemDetailsPageNav/${itemCardValue.itemID}") }
            .let {
                if (isDeleting) it.then(Modifier.alpha(0.5f)) else it
            }
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
                        .background(Brush.verticalGradient(colorStops = colorStops))
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.Bottom
                ) {

                    val itemViewCount = itemCardValue.viewCount.toString()
                    val itemLikeCount = itemCardValue.likeCount.toString()
                    val messageCount = 1500

                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(250.dp),
                        verticalArrangement = Arrangement.Bottom,
                        horizontalAlignment = Alignment.Start
                    ) {
                        UserCardTitle(
                            titleName = itemCardValue.itemName,
                            isDarkModeOn = isDarkModeOn,
                        )
                        Row(
                            modifier = Modifier.width(300.dp),
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            CountText(itemViewCount, isDarkModeOn, 0, Icons.Filled.BarChart)
                            CountText(itemLikeCount, isDarkModeOn, 0, Icons.Filled.Favorite)
                        }

                    }
                        EditButton(
                            onClick = { navController.navigate("editItemScreenNav/${itemCardValue.itemID}") },
                            isDarkModeOn = isDarkModeOn
                        )
                        DeleteButton(
                            onClick = {
                                // Perform deletion and refresh the page
                                itemViewModel.deleteItem(itemCardValue.itemID)
                                itemsLoaded.value = true// Refresh the item list page
                            },
                            isDarkModeOn = isDarkModeOn,
                            isLoading = isDeleting  // Pass the loading state
                        )

                }
            }
        }
    }
}

