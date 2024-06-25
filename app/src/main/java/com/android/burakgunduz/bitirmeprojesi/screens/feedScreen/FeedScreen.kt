package com.android.burakgunduz.bitirmeprojesi.screens.feedScreen

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.android.burakgunduz.bitirmeprojesi.screens.feedScreen.components.ItemCard
import com.android.burakgunduz.bitirmeprojesi.viewModels.ItemViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@Composable
fun FeedScreen(
    navController: NavController,
    isDarkModeOn: Boolean,
    viewModel: ItemViewModel,
    userInfo: MutableState<String?>,
    auth: FirebaseAuth,
    bottomBarVisible: MutableState<Boolean>
) {
    userInfo.value = userInfo.value ?: auth.currentUser?.uid
    val isItemsLoaded = remember { mutableStateOf(false) }
    val isItemsImagesLoaded = remember { mutableStateOf(false) }
    val itemsOnSale = viewModel.itemsOnSale.observeAsState().value
    val itemShowcaseImage = viewModel.itemShowcaseImages.observeAsState().value
    val lazyState = rememberLazyListState()

    Surface(modifier = Modifier.fillMaxSize()) {
        LaunchedEffect(isItemsLoaded) {
            viewModel.loadItems { isItemsLoaded.value = it }
        }

        if (isItemsLoaded.value) {
            LaunchedEffect(isItemsImagesLoaded) {
                viewModel.loadShowcaseImages(itemsOnSale!!) { isItemsImagesLoaded.value = it }
            }

            val itemImages = itemShowcaseImage?.map { it }
            LaunchedEffect(lazyState.isScrollInProgress) {
                if (lazyState.isScrollInProgress) {
                    delay(200)  // delay for 200 milliseconds
                    if (lazyState.isScrollInProgress) {  // check again if still scrolling
                        bottomBarVisible.value = false
                    }
                } else {
                    delay(200)  // delay for 200 milliseconds
                    if (!lazyState.isScrollInProgress) {  // check again if still not scrolling
                        bottomBarVisible.value = true
                    }
                }
            }
            LazyColumn(state = lazyState, horizontalAlignment = Alignment.CenterHorizontally) {
                itemsIndexed(itemsOnSale!!) { _, document ->
                    val toggleButtonChecked = remember { mutableStateOf(true) }
                    viewModel.checkItemIsLiked(
                        userInfo.value!!,
                        document.itemID
                    ) { toggleButtonChecked.value = it }
                    Log.e("FeedScreen", "ItemID: $toggleButtonChecked")
                    val itemImagesEqualled = itemImages?.find { it.itemID == document.itemID }?.uri
                    Log.e("FeedScreen", "ItemImages: $itemImagesEqualled")
                    ItemCard(
                        document,
                        itemImagesEqualled.toString(),
                        isDarkModeOn,
                        navController,
                        toggleButtonChecked,
                        { viewModel.addLikedItem(userInfo.value!!, document.itemID) },
                        { viewModel.removeLikedItems(userInfo.value!!, document.itemID) }
                    )
                }
            }
        } else {
            CircularProgressIndicator()
        }
    }
}