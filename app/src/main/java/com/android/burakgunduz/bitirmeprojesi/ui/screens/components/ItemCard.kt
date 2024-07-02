package com.android.burakgunduz.bitirmeprojesi.ui.screens.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.android.burakgunduz.bitirmeprojesi.viewModels.ItemCard
import kotlinx.coroutines.delay

@Composable
fun ItemCardSkeleton() {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth(0.95f)
            .size(200.dp, 250.dp)
            .padding(10.dp)
            .clip(shape = AbsoluteRoundedCornerShape(5))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            SkeletonLoader(modifier = Modifier
                .height(150.dp)
                .fillMaxWidth())
            Column(modifier = Modifier.padding(10.dp)) {
                SkeletonLoader(modifier = Modifier
                    .height(24.dp)
                    .fillMaxWidth(0.6f))
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    SkeletonLoader(modifier = Modifier
                        .height(24.dp)
                        .fillMaxWidth(0.4f))
                    SkeletonLoader(modifier = Modifier
                        .height(24.dp)
                        .fillMaxWidth(0.2f))
                }
                Spacer(modifier = Modifier.height(8.dp))
                SkeletonLoader(modifier = Modifier
                    .height(24.dp)
                    .fillMaxWidth(0.4f))
            }
        }
    }
}


@Composable
fun ItemCard(
    itemCardValue: ItemCard,
    imageUrl: String,
    isDarkModeOn: Boolean,
    navController: NavController,
    toggleButtonChecked: MutableState<Boolean>,
    likeItem: () -> Unit,
    unLikeItem: () -> Unit,
    isItemOwn: Boolean,
) {
    val isContentLoaded = remember { mutableStateOf(false) }
    Log.e("ItemCard", "ItemCard: ${isItemOwn}")
    LaunchedEffect(Unit) {
        delay(300) // Simulate loading delay
        isContentLoaded.value = true
    }

    if (isContentLoaded.value) {
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
                .clip(shape = AbsoluteRoundedCornerShape(5))
                .clickable { navController.navigate("itemDetailsPageNav/${itemCardValue.itemID}") }
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                if (imageUrl.isNotEmpty()) {
                    SubcomposeAsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(imageUrl)
                            .crossfade(true)
                            .diskCachePolicy(CachePolicy.ENABLED) // Enables disk caching
                            .memoryCachePolicy(CachePolicy.ENABLED) // Enables memory caching
                            .build(),
                        contentDescription = "Item Image",
                        contentScale = ContentScale.FillWidth,
                        alignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        val state = painter.state
                        when (state) {
                            is AsyncImagePainter.State.Loading -> {
                                SkeletonLoader(modifier = Modifier
                                    .height(150.dp)
                                    .fillMaxWidth())
                            }

                            is AsyncImagePainter.State.Empty, is AsyncImagePainter.State.Error -> {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Error,
                                        contentDescription = "Error Icon"
                                    )
                                }
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
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(1.5f), // This will take up the remaining space
                            verticalArrangement = Arrangement.Bottom,
                            horizontalAlignment = Alignment.Start
                        ) {
                            ItemCardTitle(
                                itemCardValue.itemName,
                                itemCardValue.itemPrice.toString(),
                                isDarkModeOn
                            )

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                ItemCardSubTitletext(itemCardValue.itemCategory, isDarkModeOn, 10)
                                ItemCardSubTitletext("•", isDarkModeOn, 5)
                                ItemConditions(itemCardValue.itemCondition, isDarkModeOn)
                            }
                        }
                        PriceCard(priceValue = itemCardValue.itemPrice.toString(), isDarkModeOn = isDarkModeOn)
                    }
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.Top,
                        modifier = Modifier.fillMaxWidth() // This will not take up any additional space
                    ) {
                        if (!isItemOwn) {
                            ItemLikeButton(isDarkModeOn, toggleButtonChecked, likeItem, unLikeItem)
                        }
                    }
                } else {
                    ItemCardSkeleton()
                }
            }
        }
    } else {
        ItemCardSkeleton()
    }
}
