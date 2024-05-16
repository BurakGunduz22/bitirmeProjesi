package com.android.burakgunduz.bitirmeprojesi.screens.directMessagingScreen

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import com.android.burakgunduz.bitirmeprojesi.ui.theme.fonts.archivoFonts
import com.android.burakgunduz.bitirmeprojesi.viewModels.MessageViewModel

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
    Log.e(
        "DirectMessagingScreen",
        "DirectMessagingScreen: $userIDInfo $receiverID $itemID $conversationUserID"
    )
    LaunchedEffect(Unit) {
        if (userIDInfo != null && receiverID != null && itemID != null && conversationUserID != null) {
            if (userIDInfo != receiverID) {
                viewModel.getDirectMessages(receiverID, itemID, conversationUserID)
            }
            else {
                viewModel.getDirectMessages(userIDInfo, itemID, conversationUserID)
            }
            Log.e("MessageList", messageList.toString())
        }
    }
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxHeight(0.9f)
                    .fillMaxWidth()
            ) {
                if (messageList != null) {
                    val sortedMessageList = messageList.sortedBy { it.timestamp }
                    Log.e("SortedList", sortedMessageList.toString())
                    itemsIndexed(sortedMessageList) { index, message ->
                        MessageBubble(
                            message = message.message,
                            isOwnMessage = message.senderID == userIDInfo
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
                                if (messageList!!.isEmpty()){
                                    viewModel.sendFirstMessage(
                                        userID = userIDInfo,
                                        conversationUserID = conversationUserID,
                                        itemID = itemID,
                                        messageContext = messageContext.value
                                    )
                                }
                                else{
                                    viewModel.sendMessage(
                                        userID = userIDInfo,
                                        conversationUserID = conversationUserID,
                                        itemID = itemID,
                                        messageContext = messageContext.value)
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
fun MessageBubble(message: String, isOwnMessage: Boolean) {
    val backgroundColor = if (isOwnMessage) Color.LightGray else Color.White
    val alignment = if (isOwnMessage) Alignment.CenterEnd else Alignment.CenterStart
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        contentAlignment = alignment
    ) {
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
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

class TriangleEdgeShape(val offset: Int) : Shape {

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val trianglePath = Path().apply {
            moveTo(x = 0f, y = size.height - offset)
            lineTo(x = 0f, y = size.height)
            lineTo(x = 0f + offset, y = size.height)
        }
        return Outline.Generic(path = trianglePath)
    }
}