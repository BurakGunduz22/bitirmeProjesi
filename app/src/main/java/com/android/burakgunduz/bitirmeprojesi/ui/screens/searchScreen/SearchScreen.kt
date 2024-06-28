package com.android.burakgunduz.bitirmeprojesi.ui.screens.searchScreen

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.android.burakgunduz.bitirmeprojesi.ui.screens.components.FakeTopBar
import com.android.burakgunduz.bitirmeprojesi.ui.screens.components.ItemCard
import com.android.burakgunduz.bitirmeprojesi.ui.theme.fonts.robotoFonts
import com.android.burakgunduz.bitirmeprojesi.viewModels.ItemViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun SearchScreen(
    itemViewModel: ItemViewModel,
    navController: NavController,
    isDarkModeOn: Boolean,
    userInfo: MutableState<String?>,
    auth: FirebaseAuth,
) {
    var query by remember { mutableStateOf("") }
    val isItemsLoaded = remember { mutableStateOf(false) }
    userInfo.value = userInfo.value ?: auth.currentUser?.uid
    val focusManager = LocalFocusManager.current
    LaunchedEffect(query) {
        itemViewModel.searchItemsIn(query) {
            isItemsLoaded.value = it
        }
    }
    val isItemsImagesLoaded = remember { mutableStateOf(false) }
    val itemShowcaseImage = itemViewModel.itemShowcaseImages.observeAsState().value
    val items by itemViewModel.items.collectAsState()

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 25.dp), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            FakeTopBar(navController = navController, screenName = "Search")
            TextField(
                value = query,
                onValueChange = { query = it },
                label = {
                    Text(
                        "Search",
                        fontFamily = robotoFonts,
                        fontWeight = FontWeight.Bold,
                    )
                },
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(10.dp)
                    .clip(AbsoluteRoundedCornerShape(25))
                ,keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus()
                }),
                    leadingIcon={ Icon(imageVector = Icons.Outlined.Search, contentDescription = "Search")}
                )

            if (isItemsLoaded.value) {

                LaunchedEffect(isItemsImagesLoaded) {
                    itemViewModel.loadShowcaseImages(items) { isItemsImagesLoaded.value = it }
                }
                val itemImages = itemShowcaseImage?.map { it }
                LazyColumn(horizontalAlignment = Alignment.CenterHorizontally) {
                    itemsIndexed(items) { _, document ->
                        val toggleButtonChecked =
                            remember { mutableStateOf(itemViewModel.isItemLiked(document.itemID)) }
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
                                itemViewModel.addLikedItem(userInfo.value!!, document.itemID)
                                toggleButtonChecked.value = true
                            },
                            {
                                itemViewModel.removeLikedItems(userInfo.value!!, document.itemID)
                                toggleButtonChecked.value = false
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
