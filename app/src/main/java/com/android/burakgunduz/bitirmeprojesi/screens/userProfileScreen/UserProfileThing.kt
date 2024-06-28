package com.android.burakgunduz.bitirmeprojesi.screens.userProfileScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AccountScreen() {

    Column(modifier = Modifier.fillMaxSize()) {
        Text("Your Account", modifier = Modifier.padding(top = 16.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            Text("Account Information", modifier = Modifier.weight(1f))
            Icon(
                Icons.Outlined.Person,
                contentDescription = "Navigate",
                modifier = Modifier.padding(end = 16.dp)
            )
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            Text("Listed Items", modifier = Modifier.weight(1f))
            Icon(
                Icons.Outlined.Inventory2,
                contentDescription = "Navigate",
                modifier = Modifier.padding(end = 16.dp)
            )
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            Text("Settings", modifier = Modifier.weight(1f))
            Icon(
                Icons.Outlined.Settings,
                contentDescription = "Navigate",
                modifier = Modifier.padding(end = 16.dp)
            )
        }
    }

}
