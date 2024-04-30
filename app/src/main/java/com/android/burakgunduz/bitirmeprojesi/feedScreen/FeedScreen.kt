package com.android.burakgunduz.bitirmeprojesi.feedScreen

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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference

@Composable
fun FeedScreen(storageRef: StorageReference, db: FirebaseFirestore, navController: NavController) {
    val userId: String = navController.currentBackStackEntry
        ?.arguments?.getString("userId") ?: return
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
            val items = listOf("Item 1","Item 1","Item 1","Item 1","Item 1","Item 1","Item 1","Item 1","Item 1"
                ,"Item 1","Item 1","Item 1","Item 1","Item 1","Item 1","Item 1")
            LazyColumn(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                itemsIndexed(items) { index, item ->
                    val imageUrl = remember { mutableStateOf("") }
                    LaunchedEffect(key1 = item) {
                        val storageRefer = storageRef.child("/itemImages/QBdTyhzcKl0UWKujTQLj/0.png")
                        storageRefer.downloadUrl.addOnSuccessListener {
                            imageUrl.value = it.toString()
                        }

                    }
                    if (imageUrl.value!="") {
                        ItemCard(storageRef, db, "1", imageUrl.value)
                    }else {
                        // Show a loading indicator while the image is loading
                        CircularProgressIndicator()
                    }
                }
            }



    }
}