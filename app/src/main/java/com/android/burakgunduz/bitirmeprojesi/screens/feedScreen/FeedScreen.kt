package com.android.burakgunduz.bitirmeprojesi.screens.feedScreen

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.navigation.NavController
import com.android.burakgunduz.bitirmeprojesi.screens.feedScreen.itemCard.ItemCard
import com.android.burakgunduz.bitirmeprojesi.ViewModels.ItemViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference

@Composable
fun FeedScreen(
    storageRef: StorageReference,
    db: FirebaseFirestore,
    navController: NavController,
    isDarkModeOn: Boolean,
    viewModel: ItemViewModel
) {
   val itemsOnSale = viewModel.itemsOnSale.observeAsState().value
    Surface(
        modifier = Modifier.fillMaxSize(),
    ) {
        val dataLoaded = remember { mutableStateOf(false) }
        LaunchedEffect(Unit) {
            viewModel.loadItems()
        }
        Log.e("FeedScreen", "FeedScreen: ${itemsOnSale}")
        if (itemsOnSale != null) {
            LazyColumn(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                itemsIndexed(itemsOnSale) { index, document ->
                    val imageUrl = remember { mutableStateOf("") }
                    LaunchedEffect(document) {
                        val storageRefer = storageRef.child("/itemImages/${document.itemID}/0.png")
                        storageRefer.downloadUrl.addOnSuccessListener {
                            imageUrl.value = it.toString()
                        }
                    }
                    if (imageUrl.value != "") {
                        ItemCard(
                            document,
                            imageUrl.value,
                            isDarkModeOn,
                            navController
                        )
                    } else {
                        // Show a loading indicator while the image is loading
                        CircularProgressIndicator()
                    }
                }
            }
        } else {
            // Show a loading indicator while the data is loading
            CircularProgressIndicator()
        }
    }
}