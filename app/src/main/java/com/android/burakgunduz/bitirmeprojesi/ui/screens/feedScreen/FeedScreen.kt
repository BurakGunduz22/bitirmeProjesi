package com.android.burakgunduz.bitirmeprojesi.ui.screens.feedScreen

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.navigation.NavController
import com.android.burakgunduz.bitirmeprojesi.ui.screens.components.ItemCard
import com.android.burakgunduz.bitirmeprojesi.ui.screens.components.ItemCardSkeleton
import com.android.burakgunduz.bitirmeprojesi.viewModels.ItemViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
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
    val pullToRefreshState = rememberPullToRefreshState()
    val showRefreshIndicator = remember { mutableStateOf(false) }

    val isRefreshing = remember { mutableStateOf(false) }
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(pullToRefreshState.nestedScrollConnection)
    ) {
        LaunchedEffect(isItemsLoaded) {
            viewModel.loadItems { isItemsLoaded.value = it }
        }
        LaunchedEffect(userInfo.value) {
            userInfo.value?.let {
                viewModel.loadLikedItems(it) {
                    // This will be called once liked items are loaded
                }
            }
        }
        if (pullToRefreshState.isRefreshing) {
            LaunchedEffect(true) {
                isRefreshing.value = true
                showRefreshIndicator.value = true
                isItemsLoaded.value = false
                isItemsImagesLoaded.value = false
                delay(300)
                viewModel.loadItems {
                    isItemsLoaded.value = it
                    isRefreshing.value = false
                    showRefreshIndicator.value = false
                }
            }
        }
        LaunchedEffect(isRefreshing.value) {
            if (isRefreshing.value) {
                pullToRefreshState.startRefresh()
            } else {
                pullToRefreshState.endRefresh()
            }
        }


        val itemImages = itemShowcaseImage?.map { it }
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (isItemsLoaded.value) {
                LaunchedEffect(isItemsImagesLoaded) {
                    viewModel.loadShowcaseImages(itemsOnSale!!) { isItemsImagesLoaded.value = it }
                }
                LazyColumn(state = lazyState, horizontalAlignment = Alignment.CenterHorizontally) {
                    itemsIndexed(itemsOnSale!!) { _, document ->
                        val toggleButtonChecked =
                            remember { mutableStateOf(viewModel.isItemLiked(document.itemID)) }
                        Log.e("FeedScreen", "ItemID: $toggleButtonChecked")
                        val itemImagesEqualled =
                            itemImages?.find { it.itemID == document.itemID }?.uri
                        Log.e("FeedScreen", "ItemImages: $itemImagesEqualled")
                        ItemCard(
                            document,
                            itemImagesEqualled.toString(),
                            isDarkModeOn,
                            navController,
                            toggleButtonChecked,
                            {
                                viewModel.addLikedItem(userInfo.value!!, document.itemID)
                                toggleButtonChecked.value = true
                            },
                            {
                                viewModel.removeLikedItems(userInfo.value!!, document.itemID)
                                toggleButtonChecked.value = false
                            },
                        )
                    }
                }
            } else {
                Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                    ItemCardSkeleton()
                    ItemCardSkeleton()
                    ItemCardSkeleton()
                }
            }

            PullToRefreshContainer(
                state = pullToRefreshState,
                modifier = Modifier
                    .align(Alignment.TopCenter),
            )


        }
    }
}