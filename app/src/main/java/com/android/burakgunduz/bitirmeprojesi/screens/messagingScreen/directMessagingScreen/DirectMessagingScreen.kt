package com.android.burakgunduz.bitirmeprojesi.screens.messagingScreen.directMessagingScreen

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import com.android.burakgunduz.bitirmeprojesi.ui.theme.fonts.archivoFonts
import com.android.burakgunduz.bitirmeprojesi.viewModels.MessageViewModel
import com.google.firebase.Timestamp
import java.util.Calendar

@Composable
fun DirectMessagingScreen(
    viewModel: MessageViewModel,
    userIDInfo: String?,
    navBack: NavBackStackEntry
) {
    val receiverID = navBack.arguments?.getString("receiverID")
    val itemID = navBack.arguments?.getString("itemID")
    val conversationUserID = navBack.arguments?.getString("conversationUserID")
    val messageList = viewModel.directMessages.observeAsState().value
    val messageContext = remember {
        mutableStateOf("")
    }
    val timeDif = remember { mutableIntStateOf(0) }
    val currentDate = Calendar.getInstance()
    Log.e(
        "DirectMessagingScreen",
        "DirectMessagingScreen: $userIDInfo $receiverID $itemID $conversationUserID"
    )
    LaunchedEffect(Unit) {
        if (userIDInfo != null && receiverID != null && itemID != null && conversationUserID != null) {
            if (userIDInfo != receiverID) {
                viewModel.getDirectMessages(receiverID, itemID, conversationUserID)
            } else {
                viewModel.getDirectMessages(userIDInfo, itemID, conversationUserID)
            }
            Log.e("MessageList", messageList.toString())
        }
    }
    Log.e("MessageList", messageList.toString())
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxHeight(0.9f)
                    .fillMaxWidth()
            ) {
                if (messageList != null) {
                    val sortedMessageList = messageList.sortedBy { it.timestamp }
                    itemsIndexed(sortedMessageList) { _, message ->
                        MessageBubble(
                            message = message.message,
                            isOwnMessage = message.senderID == userIDInfo,
                            messageDate = message.timestamp,
                        )
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                TextField(
                    modifier = Modifier.fillMaxWidth(0.9f),
                    value = messageContext.value,
                    onValueChange = { messageContext.value = it },
                    shape = AbsoluteRoundedCornerShape(12.dp),
                    placeholder = { Text("Type a message") },
                    trailingIcon = {
                        IconButton(onClick = {
                            if (userIDInfo != null && receiverID != null && itemID != null && conversationUserID != null) {
                                if (!messageList.isNullOrEmpty()) {
                                    viewModel.sendMessage(
                                        userID = userIDInfo,
                                        conversationUserID = conversationUserID,
                                        itemID = itemID,
                                        messageContext = messageContext.value
                                    )
                                    messageContext.value = ""
                                } else {
                                    viewModel.sendFirstMessage(
                                        userID = userIDInfo,
                                        conversationUserID = conversationUserID,
                                        itemID = itemID,
                                        messageContext = messageContext.value
                                    )
                                    messageContext.value = ""
                                }
                                viewModel.getDirectMessages(userIDInfo, itemID, conversationUserID)
                            }
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send Message"
                            )
                        }
                    }
                )

            }

        }
    }
}

@Composable
fun MessageBubble(message: String, isOwnMessage: Boolean, messageDate: Timestamp) {
    val alignment = if (isOwnMessage) Alignment.CenterEnd else Alignment.CenterStart
    val timeAlignment = if (isOwnMessage) Alignment.End else Alignment.Start
    val messageTime = Calendar.getInstance().apply {
        time = messageDate.toDate()
    }
    val messageHour = messageTime.get(Calendar.HOUR_OF_DAY)
    val messageMinutes = messageTime.get(Calendar.MINUTE)
    val messageHourMinutes = "$messageHour:$messageMinutes"
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        contentAlignment = alignment
    ) {
        Column {
            ElevatedCard(
                shape = RoundedCornerShape(
                    topStart = 48f,
                    topEnd = 48f,
                    bottomStart = if (isOwnMessage) 48f else 0f,
                    bottomEnd = if (isOwnMessage) 0f else 48f
                ),
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = message,
                    fontFamily = archivoFonts,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(
                        top = 16.dp,
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 3.dp
                    ),
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = messageHourMinutes,
                    fontFamily = archivoFonts,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .padding(6.dp)
                        .align(timeAlignment),
                    style = MaterialTheme.typography.bodySmall
                )
            }

        }


    }
}
