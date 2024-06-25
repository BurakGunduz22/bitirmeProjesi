package com.android.burakgunduz.bitirmeprojesi.screens.itemDetailsPage

import android.util.Log
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.android.burakgunduz.bitirmeprojesi.screens.itemDetailsPage.components.ImagePager
import com.android.burakgunduz.bitirmeprojesi.screens.itemDetailsPage.components.ItemCategory
import com.android.burakgunduz.bitirmeprojesi.screens.itemDetailsPage.components.ItemConditions
import com.android.burakgunduz.bitirmeprojesi.screens.itemDetailsPage.components.ItemDescription
import com.android.burakgunduz.bitirmeprojesi.screens.itemDetailsPage.components.ItemLocationMap
import com.android.burakgunduz.bitirmeprojesi.screens.itemDetailsPage.components.ItemName
import com.android.burakgunduz.bitirmeprojesi.screens.itemDetailsPage.components.ItemPrice
import com.android.burakgunduz.bitirmeprojesi.screens.itemDetailsPage.components.SendMessageToSellerButton
import com.android.burakgunduz.bitirmeprojesi.viewModels.ItemViewModel
import kotlinx.coroutines.delay


@Composable
fun ItemDetailsPage(
    backStackEntry: NavBackStackEntry,
    navController: NavController,
    viewModel: ItemViewModel,
    isDarkModeOn: Boolean,
    currentUserID: String?,
) {
    val itemDetailsViewModel = viewModel.itemDetails.observeAsState().value
    val itemImages = viewModel.itemImages.observeAsState().value
    val itemId = backStackEntry.arguments?.getString("itemId")
    val context = LocalContext.current
    val isMapLoaded = remember { mutableStateOf(false) }
    val isMapLoadedAgain = remember { mutableStateOf(true) }
    val isItemSame = remember { mutableStateOf(false) }
    val toggleButtonChecked = remember { mutableStateOf(false) }
    val sellerName = viewModel.sellerProfile.observeAsState().value
    val isTouched = remember { mutableStateOf(false) }
    isItemSame.value = !(itemDetailsViewModel == null || itemDetailsViewModel.itemID != itemId)

    LaunchedEffect(itemId) {
        if (itemId != null) {
            viewModel.loadItemDetails(itemId, isItemSame.value)
            viewModel.loadItemImages(itemId, isItemSame.value)
            if (currentUserID != null) {
                viewModel.checkItemIsLiked(currentUserID, itemId) {
                    toggleButtonChecked.value = it
                }
            }
        }
    }

    LaunchedEffect(isMapLoaded.value) {
        if (!isMapLoaded.value) {
            delay(500)
            isMapLoaded.value = true
        }
    }

    LaunchedEffect(isMapLoadedAgain.value) {
        if (!isMapLoadedAgain.value) {
            delay(500)
            isMapLoadedAgain.value = true
        }
    }

    val lazyListState = rememberLazyListState()

    Surface(modifier = Modifier.fillMaxSize()) {
        if (itemDetailsViewModel != null) {
            Column(modifier = Modifier.fillMaxSize()) {
                ImagePager(
                    imageUris = itemImages!!,
                    navController,
                    isMapLoadedAgain,
                    isTouched
                )
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        LazyColumn(
                            state = lazyListState,
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.Top,
                            horizontalAlignment = Alignment.Start
                        ) {
                            Log.e("toggleButton", "ItemDetails: ${toggleButtonChecked.value}")
                            if (isMapLoaded.value) {
                                item {
                                    ItemName(itemName = itemDetailsViewModel.itemName)
                                }
//                                item {
//                                    if (sellerName?.value != null) {
//                                        Log.e("SellerName", "SellerName: $sellerName")
//                                        SellerProfile(
//                                            sellerProfileName = sellerName.value.name,
//                                            sellerID = itemDetailsViewModel.userID,
//                                            navController = navController,
//                                            itemViewModel = viewModel
//                                        )
//                                    }
//                                }
                                item {

                                }
                                item {
                                    Row(modifier = Modifier.fillMaxWidth().padding(top = 10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                                        ItemCategory(
                                            itemCategory = itemDetailsViewModel.itemCategory,
                                            itemSubCategory = itemDetailsViewModel.itemSubCategory
                                        )
                                        ItemPrice(itemPrice = itemDetailsViewModel.itemPrice)
                                    }

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
                                            context = context,
                                            isDarkModeOn = isDarkModeOn
                                        )
                                    }
                                }
                                item { Spacer(modifier = Modifier.height(50.dp)) }
                            } else {
                                item { LinearProgressIndicator() }
                            }
                        }
                        SendMessageToSellerButton(
                            isDarkModeOn,
                            navController,
                            itemDetailsViewModel.userID,
                            itemDetailsViewModel.itemID,
                            currentUserID,
                            toggleButtonChecked,
                            {
                                viewModel.addLikedItem(
                                    currentUserID!!,
                                    itemDetailsViewModel.itemID
                                )
                            },
                            {
                                viewModel.removeLikedItems(
                                    currentUserID!!,
                                    itemDetailsViewModel.itemID
                                )
                            }
                        )
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



