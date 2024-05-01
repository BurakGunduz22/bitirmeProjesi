package com.android.burakgunduz.bitirmeprojesi.itemDetailsPage

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference

@Composable
fun ItemDetailsPage(
    backStackEntry: NavBackStackEntry,
    fireStoreRef: FirebaseFirestore,
    fireStorageRef: StorageReference
) {
    val itemId = backStackEntry.arguments?.getString("itemId")?.replace("{", "")?.replace("}", "")
    val itemDetails = remember { mutableStateOf<Map<String, Any>?>(null) }

    Log.e("itemId", "$itemId")
    LaunchedEffect(itemId) {
        fireStoreRef.collection("itemsOnSale").document(itemId!!)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    itemDetails.value = document.data
                } else {
                    println("No such document")
                }
            }
            .addOnFailureListener { exception ->
                println("Error getting document: $exception")
            }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        if (itemDetails.value != null) {
            // Display the item details
            LazyColumn {
                itemDetails.value?.forEach { entry ->
                    item {
                        Text(text = "${entry.key}: ${entry.value}")
                    }
                }
            }
        } else {
            // Show a loading indicator while the data is loading
            CircularProgressIndicator()
        }
    }
}