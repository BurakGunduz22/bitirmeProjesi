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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.android.burakgunduz.bitirmeprojesi.ItemCategorySkeleton
import com.android.burakgunduz.bitirmeprojesi.ItemConditionSkeleton
import com.android.burakgunduz.bitirmeprojesi.ItemDescriptionSkeleton
import com.android.burakgunduz.bitirmeprojesi.ItemLocationMapSkeleton
import com.android.burakgunduz.bitirmeprojesi.ItemNameSkeleton
import com.android.burakgunduz.bitirmeprojesi.SkeletonLoader
import com.android.burakgunduz.bitirmeprojesi.screens.itemDetailsPage.components.ImagePager
import com.android.burakgunduz.bitirmeprojesi.screens.itemDetailsPage.components.ItemCategory
import com.android.burakgunduz.bitirmeprojesi.screens.itemDetailsPage.components.ItemConditions
import com.android.burakgunduz.bitirmeprojesi.screens.itemDetailsPage.components.ItemDescription
import com.android.burakgunduz.bitirmeprojesi.screens.itemDetailsPage.components.ItemLocationMap
import com.android.burakgunduz.bitirmeprojesi.screens.itemDetailsPage.components.ItemName
import com.android.burakgunduz.bitirmeprojesi.screens.itemDetailsPage.components.ItemPrice
import com.android.burakgunduz.bitirmeprojesi.screens.itemDetailsPage.components.SellerProfile
import com.android.burakgunduz.bitirmeprojesi.screens.itemDetailsPage.components.SendMessageToSellerButton
import com.android.burakgunduz.bitirmeprojesi.viewModels.ItemViewModel
import com.android.burakgunduz.bitirmeprojesi.viewModels.MessageViewModel
import kotlinx.coroutines.delay


@Composable
fun ItemDetailsPage(
    backStackEntry: NavBackStackEntry,
    navController: NavController,
    viewModel: ItemViewModel,
    isDarkModeOn: Boolean,
    currentUserID: String?,
    messageViewModel: MessageViewModel
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
        delay(500)
        if (itemId != null) {
            viewModel.loadItemDetails(itemId, isItemSame.value)
            viewModel.loadItemImages(itemId, isItemSame.value)
            if (currentUserID != null) {
                viewModel.checkItemIsLiked(currentUserID, itemId) {
                    toggleButtonChecked.value = it
                }
            }
            isMapLoadedAgain.value = false
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
        Column(modifier = Modifier.fillMaxSize()) {
            Box {
                if (itemImages != null) {
                    ImagePager(
                        imageUris = itemImages,
                        navController,
                        isMapLoadedAgain,
                        isTouched
                    )
                } else {
                    SkeletonLoader(modifier = Modifier
                        .height(200.dp)
                        .fillMaxWidth())
                }
                if (!isTouched.value) {
                    IconButton(
                        onClick = {
                            navController.navigateUp()
                        }, modifier = Modifier.clip(CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            }
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
                        if (itemDetailsViewModel != null) {
                            item { ItemName(itemName = itemDetailsViewModel.itemName) }
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    ItemCategory(
                                        itemCategory = itemDetailsViewModel.itemCategory,
                                        itemSubCategory = itemDetailsViewModel.itemSubCategory
                                    )
                                    ItemPrice(itemPrice = itemDetailsViewModel.itemPrice)
                                }
                            }
                            item {
                                Spacer(modifier = Modifier.height(8.dp)) // Add space above the divider
                                HorizontalDivider(
                                    color = MaterialTheme.colorScheme.secondary, // Change color of the divider
                                    thickness = (1.5).dp,
                                    modifier = Modifier.padding(horizontal = 10.dp)// Increase thickness of the divider
                                )
                                Spacer(modifier = Modifier.height(8.dp)) // Add space below the divider
                            }
                            item {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    ItemConditions(itemCondition = itemDetailsViewModel.itemCondition)
                                    if (sellerName?.value != null) {
                                        Log.e("SellerName", "SellerName: $sellerName")
                                        SellerProfile(
                                            sellerProfileName = sellerName.value.name,
                                            sellerID = itemDetailsViewModel.userID,
                                            navController = navController,
                                            itemViewModel = viewModel
                                        )
                                    }
                                }
                            }
                            item { ItemDescription(itemDesc = itemDetailsViewModel.itemDesc) }
                            if (isMapLoadedAgain.value) {
                                item {
                                    ItemLocationMap(
                                        itemDetailsFor = itemDetailsViewModel,
                                        context = context,
                                        isDarkModeOn = isDarkModeOn
                                    )
                                }
                            } else {
                                item { ItemLocationMapSkeleton() }
                            }
                            item { Spacer(modifier = Modifier.height(50.dp)) }
                        } else {
                            item { ItemNameSkeleton() }
                            item { Spacer(modifier = Modifier.height(16.dp)) }
                            item { ItemCategorySkeleton() }
                            item { Spacer(modifier = Modifier.height(16.dp)) }
                            item { ItemConditionSkeleton() }
                            item { Spacer(modifier = Modifier.height(16.dp)) }
                            item { ItemDescriptionSkeleton() }
                            item { Spacer(modifier = Modifier.height(16.dp)) }
                            item { ItemLocationMapSkeleton() }
                        }
                    }
                    if (itemImages != null) {
                        SendMessageToSellerButton(
                            isDarkModeOn,
                            navController,
                            itemDetailsViewModel?.userID ?: "",
                            itemDetailsViewModel?.itemID ?: "",
                            currentUserID,
                            toggleButtonChecked,
                            {
                                if (currentUserID != null && itemDetailsViewModel != null) {
                                    viewModel.addLikedItem(
                                        currentUserID,
                                        itemDetailsViewModel.itemID
                                    )
                                }
                            },
                            {
                                if (currentUserID != null && itemDetailsViewModel != null) {
                                    viewModel.removeLikedItems(
                                        currentUserID,
                                        itemDetailsViewModel.itemID
                                    )
                                }
                            },
                            sellerName?.value?.name ?: "",
                            itemName = itemDetailsViewModel?.itemName ?: "",
                        )
                    }
                }
            }
        }
    }
}

