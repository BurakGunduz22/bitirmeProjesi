package com.android.burakgunduz.bitirmeprojesi.feedScreen

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.android.burakgunduz.bitirmeprojesi.feedScreen.itemCard.ItemCard
import com.google.firebase.appcheck.internal.util.Logger.TAG
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference

@Composable
fun FeedScreen(
    storageRef: StorageReference,
    db: FirebaseFirestore,
    navController: NavController,
    isDarkModeOn: Boolean
) {
    val userId: String = navController.currentBackStackEntry
        ?.arguments?.getString("userId") ?: return
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        val items = remember { mutableStateOf(listOf<DocumentSnapshot>()) }
        val dataLoaded = remember { mutableStateOf(false) }
        LaunchedEffect(Unit) {
            db.collection("itemsOnSale")
                .get()
                .addOnSuccessListener { result ->
                    items.value = result.documents
                    dataLoaded.value = true
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents.", exception)
                }
        }
        if (dataLoaded.value) {
            LazyColumn(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                itemsIndexed(items.value) { index, document ->
                    val imageUrl = remember { mutableStateOf("") }
                    LaunchedEffect(key1 = document) {
                        val storageRefer = storageRef.child("/itemImages/${document.id}/0.png")
                        storageRefer.downloadUrl.addOnSuccessListener {
                            imageUrl.value = it.toString()
                        }
                    }
                    if (imageUrl.value != "") {
                        ItemCard(
                            storageRef,
                            db,
                            document.id,
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