package com.android.burakgunduz.bitirmeprojesi

import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun BottomNavigationBar() {
    BottomAppBar {
        Text(text = "Feed")
        Text(text = "Search")
        Text(text = "Add Item")
        Text(text = "Favorites")
        Text(text = "Profile")
    }
}