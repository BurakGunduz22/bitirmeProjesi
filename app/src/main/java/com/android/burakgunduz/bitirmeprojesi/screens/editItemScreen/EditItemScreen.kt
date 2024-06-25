package com.android.burakgunduz.bitirmeprojesi.screens.editItemScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.android.burakgunduz.bitirmeprojesi.screens.editItemScreen.subScreens.EditItemDetails
import com.android.burakgunduz.bitirmeprojesi.screens.itemDetailsPage.components.ImagePager
import com.android.burakgunduz.bitirmeprojesi.viewModels.ItemViewModel
import kotlinx.coroutines.delay

@Composable
fun EditItemScreen(
    itemViewModel: ItemViewModel,
    navController: NavController,
    backStackEntry: NavBackStackEntry,
    currentUserID: String?
) {
    val itemDetailsViewModel = itemViewModel.itemDetails.observeAsState().value
    val itemImages = itemViewModel.itemImages.observeAsState().value
    val itemId = backStackEntry.arguments?.getString("itemId")
    val isMapLoaded = remember { mutableStateOf(false) }
    val isMapLoadedAgain = remember { mutableStateOf(true) }
    val isItemSame = remember { mutableStateOf(false) }
    val toggleButtonChecked = remember { mutableStateOf(false) }
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
                        EditItemDetails(
                            itemDetails = itemDetailsViewModel,
                            itemViewModel = itemViewModel,
                            navController = navController
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