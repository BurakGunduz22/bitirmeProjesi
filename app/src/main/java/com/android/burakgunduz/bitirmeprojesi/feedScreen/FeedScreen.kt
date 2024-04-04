package com.android.burakgunduz.bitirmeprojesi.feedScreen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.android.burakgunduz.bitirmeprojesi.feedScreen.itemCard.ItemCard
import com.google.firebase.storage.StorageReference

@Composable
fun FeedScreen(storageRef: StorageReference, navController: NavController) {
    val userId: String = navController.currentBackStackEntry
        ?.arguments?.getString("userId") ?: return
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {

        ItemCard(storageRef, userId)
    }
}