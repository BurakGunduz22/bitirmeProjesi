package com.android.burakgunduz.bitirmeprojesi.screens.itemDetailsPage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.android.burakgunduz.bitirmeprojesi.screens.itemDetailsPage.components.ImagePager
import com.android.burakgunduz.bitirmeprojesi.screens.itemDetailsPage.components.ItemConditions
import com.android.burakgunduz.bitirmeprojesi.screens.itemDetailsPage.components.ItemDescription
import com.android.burakgunduz.bitirmeprojesi.screens.itemDetailsPage.components.ItemLocationMap
import com.android.burakgunduz.bitirmeprojesi.screens.itemDetailsPage.components.ItemName
import com.android.burakgunduz.bitirmeprojesi.screens.itemDetailsPage.components.ItemPrice
import com.android.burakgunduz.bitirmeprojesi.ViewModels.ItemViewModel
import kotlinx.coroutines.delay


@Composable
fun ItemDetailsPage(
    backStackEntry: NavBackStackEntry,
    navController: NavController,
    viewModel: ItemViewModel
) {
    val itemDetailsViewModel = viewModel.itemDetails.value
    val itemImages = viewModel.itemImages.value
    val itemId = backStackEntry.arguments?.getString("itemId")?.replace("{", "")?.replace("}", "")
    val context = LocalContext.current
    val isMapLoaded = remember { mutableStateOf(false) }
    val isMapLoadedAgain = remember { mutableStateOf(true) }
    val isItemSame = remember { mutableStateOf(false) }
    if (itemDetailsViewModel == null || itemDetailsViewModel.itemID != itemId) {
        isItemSame.value = false
    }
    else{
        isItemSame.value = true
    }
    LaunchedEffect(itemId) {
        if (itemId != null) {
            viewModel.loadItemDetails(itemId,isItemSame.value)
            viewModel.loadItemImages(itemId,isItemSame.value)
        }
    }

    LaunchedEffect(isMapLoaded.value) {
        if (!isMapLoaded.value) {
            delay(500) // delay for 3 seconds
            isMapLoaded.value = true
        }
    }
    LaunchedEffect(isMapLoadedAgain.value) {
        if (!isMapLoadedAgain.value) {
            delay(500) // delay for 3 seconds
            isMapLoadedAgain.value = true
        }
    }
    Surface(modifier = Modifier.fillMaxSize()) {
        if (itemDetailsViewModel != null) {
            Column(modifier = Modifier.fillMaxSize()) {
                ImagePager(
                    imageUris = itemImages!!,
                    navController,
                    isMapLoadedAgain
                )
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.Start
                    ) {
                        if (isMapLoaded.value) {
                            item {
                                ItemName(itemName = itemDetailsViewModel.itemName)
                            }
                            item {
                                ItemPrice(itemPrice = itemDetailsViewModel.itemPrice)
                            }
                            item {
                                ItemConditions(itemCondition = itemDetailsViewModel.itemCondition)
                            }
                            item {
                                ItemDescription(itemDesc = itemDetailsViewModel.itemDesc)
                            }
                            if (isMapLoadedAgain.value) {
                                item {
                                    ItemLocationMap(
                                        itemDetailsFor = itemDetailsViewModel,
                                        context = context
                                    )
                                }
                            }
                        } else {
                            item { LinearProgressIndicator() }
                        }
                    }
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize()) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .size(360.dp, 10.dp)
                        .align(Alignment.Center)
                )
            }
        }
    }

}


