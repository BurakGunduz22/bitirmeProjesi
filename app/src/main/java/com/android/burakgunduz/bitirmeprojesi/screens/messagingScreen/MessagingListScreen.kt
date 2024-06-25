package com.android.burakgunduz.bitirmeprojesi.screens.messagingScreen

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.android.burakgunduz.bitirmeprojesi.FakeTopBar
import com.android.burakgunduz.bitirmeprojesi.screens.messagingScreen.components.MessageCard
import com.android.burakgunduz.bitirmeprojesi.viewModels.MessageViewModel
import com.google.firebase.storage.StorageReference

@Composable
fun MessagingListScreen(
    storageRef: StorageReference,
    messageViewModel: MessageViewModel,
    userIDInfo: MutableState<String?>,
    navController: NavController,
    isDarkModeOn: Boolean
) {
    val messageList = messageViewModel.messageList.observeAsState().value
    LaunchedEffect(Unit) {
        if (userIDInfo.value != null) {
            messageViewModel.getAllMessages(userIDInfo.value!!)
            Log.e("MessageList", messageList.toString())
        }
    }
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            FakeTopBar(navController, "Messages")
            if (messageList != null) {
                LazyColumn(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    itemsIndexed(messageList) { index, message ->
                        Log.e("UserName", "UserName: $index")

                        val imageUrl = remember { mutableStateOf("") }
                        LaunchedEffect(message) {
                            val storageRefer =
                                storageRef.child("/itemImages/${message.itemID}/0.png")
                            storageRefer.downloadUrl.addOnSuccessListener {
                                imageUrl.value = it.toString()
                            }
                        }
                        MessageCard(
                            messageCardValue = message,
                            isDarkModeOn = isDarkModeOn,
                            imageUrl = imageUrl.value,
                            navController = navController,
                            userIDInfo = userIDInfo.value,
                            messageViewModel = messageViewModel
                        )
                    }
                }

            } else {
                CircularProgressIndicator()
            }
        }
    }
}