package com.android.burakgunduz.bitirmeprojesi.screens.favoriteScreen

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.android.burakgunduz.bitirmeprojesi.FakeTopBar
import com.android.burakgunduz.bitirmeprojesi.screens.feedScreen.components.ItemCard
import com.android.burakgunduz.bitirmeprojesi.viewModels.ItemViewModel


@Composable
fun FavoriteScreen(
    navController: NavController,
    viewModel: ItemViewModel,
    isDarkModeOn: Boolean,
    userID: String
) {
    val isItemsLoaded = remember { mutableStateOf(false) }
    val isItemsImagesLoaded = remember { mutableStateOf(false) }
    val itemsOnSale = viewModel.itemsOnSale.observeAsState().value
    val itemShowcaseImage = viewModel.itemShowcaseImages.observeAsState().value
    LaunchedEffect(isItemsLoaded) {
        viewModel.checkLikedItems(userID) {
            isItemsLoaded.value = it
        }
    }
    Log.e("LikedItems", userID)

    Surface(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            FakeTopBar(navController, "Liked Items")
            if (isItemsLoaded.value) {
                val itemList = itemsOnSale?.map { it }
                LaunchedEffect(isItemsImagesLoaded) {
                    viewModel.loadShowcaseImages(itemsOnSale!!) {
                        isItemsImagesLoaded.value = it
                    }
                }
                val itemImages = itemShowcaseImage?.map { it }
                LazyColumn(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    itemsIndexed(itemList!!) { index, document ->
                        val toggleButtonChecked = remember { mutableStateOf(false) }

                        viewModel.checkItemIsLiked(userID, document.itemID) {
                            toggleButtonChecked.value = it
                        }
                        Log.e("Resim", "FeedScreen: $itemImages")
                        val itemImagesEqualled =
                            itemImages?.find { it.itemID == document.itemID }?.uri
                        ItemCard(
                            document,
                            itemImagesEqualled.toString(),
                            isDarkModeOn,
                            navController,
                            toggleButtonChecked,
                            {
                                viewModel.addLikedItem(
                                    userID,
                                    document.itemID
                                )
                            },
                            {
                                viewModel.removeLikedItems(
                                    userID,
                                    document.itemID
                                )
                            },
                        )
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(modifier = Modifier.size(100.dp))
                }
            }
        }
    }
}