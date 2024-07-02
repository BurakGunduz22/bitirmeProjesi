package com.android.burakgunduz.bitirmeprojesi.ui.screens.userProfileScreen.subScreens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.android.burakgunduz.bitirmeprojesi.ui.screens.components.FakeTopBar
import com.android.burakgunduz.bitirmeprojesi.ui.screens.components.UserListedItemCard
import com.android.burakgunduz.bitirmeprojesi.viewModels.ItemViewModel
import com.google.firebase.storage.StorageReference

@Composable
fun UserListedItems(
    itemViewModel: ItemViewModel,
    storageRef: StorageReference,
    isDarkModeOn: Boolean,
    navController: NavController,
    backStackEntry: NavBackStackEntry,
) {
    val itemsLoaded = remember { mutableStateOf(false) }
    val sellerID = backStackEntry.arguments?.getString("sellerID")
    val userInfosFar = backStackEntry.arguments?.getString("userInfosFar")
    val userItems by itemViewModel.itemsOnSale.observeAsState()
    Log.e("UserListedItems", "UserListedItems: ${itemsLoaded.value}")
    Log.e("UserListedItems", "UserListedItems: ${sellerID}")
    LaunchedEffect(itemsLoaded) {
        if (!itemsLoaded.value) {
            Log.e("UserListedItems", "UserListedItems: ${sellerID}")
            itemViewModel.getSellerItems(sellerID!!)
            itemsLoaded.value = true
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier=Modifier.fillMaxSize()) {
            FakeTopBar(navController = navController, screenName = "Listed Items")
            if (userItems != null) {
                LazyColumn(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    itemsIndexed(userItems!!) { _, document ->
                        val imageUrl = remember { mutableStateOf("") }
                        LaunchedEffect(document) {
                            val storageRefer =
                                storageRef.child("/itemImages/${document.itemID}/0.png")

                            storageRefer.downloadUrl
                                .addOnSuccessListener {
                                    imageUrl.value = it.toString()
                                    Log.e("UserListedItems", "UserListedItems: ${imageUrl.value}")

                                }
                                .addOnFailureListener {
                                    // Fallback to a placeholder image if the requested image is not found
                                    imageUrl.value = "https://via.placeholder.com/150"
                                }
                        }
                        if (imageUrl.value.isNotEmpty()) {
                            UserListedItemCard(
                                document,
                                itemViewModel,
                                imageUrl.value,
                                isDarkModeOn,
                                navController,
                                userInfosFar ?: "",
                                itemsLoaded
                            )
                        } else {
                            // Show a loading indicator while the image is loading
                            CircularProgressIndicator()
                        }
                    }
                }
            } else {
                // Show a loading indicator while the items are loading
                CircularProgressIndicator()
            }
        }
    }
}
