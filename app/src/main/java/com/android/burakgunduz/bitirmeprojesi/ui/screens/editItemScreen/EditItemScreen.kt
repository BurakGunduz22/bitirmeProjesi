package com.android.burakgunduz.bitirmeprojesi.ui.screens.editItemScreen

import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.android.burakgunduz.bitirmeprojesi.ui.screens.editItemScreen.subScreens.EditItemDetails
import com.android.burakgunduz.bitirmeprojesi.ui.screens.listItemForSale.subScreens.ImageUploader
import com.android.burakgunduz.bitirmeprojesi.viewModels.ItemViewModel
import com.android.burakgunduz.bitirmeprojesi.viewModels.LocationViewModel
import com.android.burakgunduz.bitirmeprojesi.viewModels.NamedUri
import kotlinx.coroutines.delay

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EditItemScreen(
    itemViewModel: ItemViewModel,
    navController: NavController,
    backStackEntry: NavBackStackEntry,
    currentUserID: String?,
    locationViewModel: LocationViewModel
) {
    val itemDetailsViewModel = itemViewModel.itemDetails.observeAsState().value
    val countryNames = locationViewModel.countryNames.observeAsState()
    val itemImages = itemViewModel.itemImages.value
    val itemImageList = itemImages?.sortedBy { it.name }
        ?.let { uris -> ensureSixElements(uris.map { it.uri }) }
    val itemId = backStackEntry.arguments?.getString("itemId")
    val isMapLoaded = remember { mutableStateOf(false) }
    val isMapLoadedAgain = remember { mutableStateOf(true) }
    val isItemSame = remember { mutableStateOf(false) }
    val toggleButtonChecked = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    rememberScrollState()
    val isTouched = remember { mutableStateOf(false) }
    isItemSame.value = !(itemDetailsViewModel == null || itemDetailsViewModel.itemID != itemId)
    LaunchedEffect(itemId) {
        if (itemId != null) {
            itemViewModel.loadItemDetails(itemId, isItemSame.value)
            itemViewModel.loadItemImages(itemId, isItemSame.value)
            if (currentUserID != null) {
                itemViewModel.checkItemIsLiked(currentUserID, itemId) {
                    toggleButtonChecked.value = it
                }
            }
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

    val pagerState = rememberPagerState { 3 }
    val selectedTabIndex = remember { mutableIntStateOf(0) }
    val emptyNamedUri = NamedUri("", Uri.EMPTY)
    LaunchedEffect(key1 = selectedTabIndex) {
        pagerState.animateScrollToPage(selectedTabIndex.intValue)
    }

    LaunchedEffect(key1 = pagerState.currentPage) {
            selectedTabIndex.intValue = pagerState.currentPage
    }

    val emptyNamedUriList = listOf(emptyNamedUri)
    Surface(modifier = Modifier.fillMaxSize()) {

        if (itemDetailsViewModel != null) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                HorizontalPager(state = pagerState, userScrollEnabled = false) { page ->
                    when (page) {
                        1 -> {
                            ImageUploader(
                                imageUri = itemImageList ?: MutableList(6) { Uri.EMPTY },
                                itemViewModel = itemViewModel,
                                itemID = itemId ?: "",
                                coroutineScope = coroutineScope,
                                locationViewModel = locationViewModel,
                                countryNames = countryNames
                            )
                        }

                        0 -> {
                            EditItemDetails(
                                itemDetails = itemDetailsViewModel,
                                itemViewModel = itemViewModel,
                                navController = navController
                            )
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

fun ensureSixElements(list: List<Uri>): MutableList<Uri> {
    return if (list.size < 6) {
        val newList = list.toMutableList()
        while (newList.size < 6) {
            newList.add(Uri.EMPTY)
        }
        newList
    } else {
        list.toMutableList()
    }
}