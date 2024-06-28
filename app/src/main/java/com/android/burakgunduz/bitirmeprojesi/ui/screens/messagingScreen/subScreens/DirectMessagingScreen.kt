package com.android.burakgunduz.bitirmeprojesi.ui.screens.messagingScreen.subScreens

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.android.burakgunduz.bitirmeprojesi.ui.screens.components.FakeTopBar
import com.android.burakgunduz.bitirmeprojesi.ui.theme.fonts.archivoFonts
import com.android.burakgunduz.bitirmeprojesi.viewModels.ItemViewModel
import com.android.burakgunduz.bitirmeprojesi.viewModels.Message
import com.android.burakgunduz.bitirmeprojesi.viewModels.MessageViewModel
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun DirectMessagingScreen(
    viewModel: MessageViewModel,
    userIDInfo: String?,
    navBack: NavBackStackEntry,
    navController: NavController,
    itemViewModel: ItemViewModel,
) {
    // Retrieve message list from ViewModel
    val messageList = viewModel.directMessages.observeAsState().value
    val messageListEdit = remember { mutableStateOf(messageList) }
    val userName = remember { mutableStateOf("") }
    // State for the current typed message
    val messageContext = remember { mutableStateOf("") }
    val itemIDForUser = remember {
        mutableStateOf("")
    }
    val userIDofItem = remember {
        mutableStateOf("")
    }
    val itemImage = remember {
        mutableStateOf(Uri.EMPTY)
    }
    val itemName = remember {
        mutableStateOf("")
    }
    val sellerProfile = itemViewModel.sellerProfile.observeAsState().value
    val sellerImage = itemViewModel.sellerImage.observeAsState().value
    // Scroll state for LazyColumn
    val scrollState = rememberLazyListState()

    // Launch effect to load messages and scroll to bottom
    LaunchedEffect(messageListEdit) {
        scrollState.scrollToItem(messageListEdit.value?.size ?: 0)
    }

    // Launch effect to load messages when necessary
    LaunchedEffect(Unit) {
        navBack.arguments?.let { args ->
            val receiverID = args.getString("receiverID")
            val itemID = args.getString("itemID")
            itemIDForUser.value = itemID.toString()
            val conversationUserID = args.getString("conversationUserID")
            userName.value = args.getString("messagerName").toString()
            itemName.value = args.getString("itemName").toString()

            if (userIDInfo != null && receiverID != null && itemID != null && conversationUserID != null) {
                if (userIDInfo != receiverID) {
                    viewModel.getDirectMessages(receiverID, itemID, conversationUserID)
                } else {
                    viewModel.getDirectMessages(userIDInfo, itemID, conversationUserID)
                }
            }
        }
    }
    LaunchedEffect(Unit) {
        itemViewModel.getUserIDFromItemID(itemIDForUser.value) {
            userIDofItem.value = it ?: ""
            itemViewModel.getSellerProfile(userIDofItem.value)
        }
        itemViewModel.loadShowCaseImage(itemIDForUser.value) {
            itemImage.value = it
        }

    }
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            FakeTopBar(
                navController = navController,
                isItDirectMessage = true,
                userName = userName.value ?: "",
                userImageUrl = sellerImage ?: Uri.EMPTY,
                sellerID = userIDofItem.value
            )
            OutlinedCard(
                modifier = Modifier
                    .fillMaxHeight(0.9f)
                    .fillMaxWidth(0.9f), // Set elevation to 0 for a flat look
                colors = CardDefaults.outlinedCardColors(
                    containerColor = Color.Transparent
                ), // Make content color transparent
                border = BorderStroke(1.dp, Color.LightGray) // Add an outline with light gray color
            ) {
                ElevatedCard(
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { navController.navigate("itemDetailsPageNav/${itemIDForUser.value}") }
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                    ) {
                        // Display item image
                        sellerImage?.let { imageUrl ->
                            // Assuming you have a composable function to load image from Uri
                            // Replace this with your actual image loading logic
                            SubcomposeAsyncImage(
                                model = itemImage.value,
                                contentDescription = "ProfilePicture",
                                contentScale = ContentScale.FillWidth,
                                modifier = Modifier
                                    .padding(10.dp)
                                    .size(100.dp)
                                    .clip(shape = AbsoluteRoundedCornerShape(8))

                            ) {
                                val state = painter.state
                                when (state) {
                                    is AsyncImagePainter.State.Loading -> {
                                        CircularProgressIndicator()
                                    }

                                    is AsyncImagePainter.State.Empty, is AsyncImagePainter.State.Error -> {
                                        Icon(
                                            imageVector = Icons.Outlined.AccountCircle,
                                            contentDescription = "Person"
                                        )
                                    }

                                    else -> {
                                        SubcomposeAsyncImageContent()
                                    }
                                }
                            }
                        }
                        // Display item name
                        Text(
                            text = itemName.value,
                            style = MaterialTheme.typography.titleLarge,
                            fontFamily = archivoFonts,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .width(250.dp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = scrollState
                ) {
                    val sortedMessageList = messageListEdit.value?.sortedBy { it.timestamp }
                    sortedMessageList?.groupByDay()?.forEach { (day, messages) ->
                        // Date separator
                        item {
                            DateSeparator(date = day)
                        }
                        // Messages for the day
                        itemsIndexed(messages) { _, message ->
                            MessageBubble(
                                message = message.message,
                                isOwnMessage = message.senderID == userIDInfo,
                                messageDate = message.timestamp,
                            )
                        }
                    }
                }
            }
            // Input area for typing messages
            MessageInputArea(
                messageContext = messageContext,
                userIDInfo = userIDInfo,
                navBack = navBack,
                viewModel = viewModel,
                messageList = messageListEdit
            )
        }
    }
}

@Composable
fun DateSeparator(date: Date) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val dateString = dateFormat.format(date)

    Row(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            color = Color.LightGray,
            modifier = Modifier
                .height(1.dp)
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {}
        Text(
            text = dateString,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = 8.dp),
            fontFamily = archivoFonts,
            fontWeight = FontWeight.Normal,
        )
        Surface(
            color = Color.LightGray,
            modifier = Modifier
                .height(1.dp)
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {}
    }
}

@Composable
fun MessageBubble(message: String, isOwnMessage: Boolean, messageDate: Timestamp) {
    val messageTime = Calendar.getInstance().apply {
        time = messageDate.toDate()
    }
    val alignment = if (isOwnMessage) Alignment.CenterEnd else Alignment.CenterStart
    val messageHour = messageTime.get(Calendar.HOUR_OF_DAY)
    val messageMinutes = messageTime.get(Calendar.MINUTE)
    val messageHourMinutes = "$messageHour:${
        String.format(
            "%02d",
            messageMinutes
        )
    }" // Formats minutes with leading zeros

    val bubbleColor = if (isOwnMessage) Color(0xFFAA69FD) else
        Color(0xFF7203FF)
    val textColor = if (isOwnMessage) Color.Black else Color.White

    Box(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        contentAlignment = alignment
    ) {
        ElevatedCard(
            shape = RoundedCornerShape(
                topStart = 48f,
                topEnd = 48f,
                bottomStart = if (isOwnMessage) 48f else 0f,
                bottomEnd = if (isOwnMessage) 0f else 48f
            ),
            colors = CardDefaults.elevatedCardColors(
                containerColor = bubbleColor
            ),
            modifier = Modifier.padding(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 4.dp),
                    fontFamily = archivoFonts,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = messageHourMinutes,
                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFC7C7C7)),
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageInputArea(
    messageContext: MutableState<String>,
    userIDInfo: String?,
    navBack: NavBackStackEntry,
    viewModel: MessageViewModel,
    messageList: MutableState<List<Message>?>
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        TextField(
            value = messageContext.value,
            onValueChange = {
                if (it.length <= 250) {
                    messageContext.value = it
                }
            },
            modifier = Modifier
                .weight(1f)
                .padding(8.dp),
            placeholder = {
                Text(
                    "Type a message...",
                    fontFamily = archivoFonts,
                    fontWeight = FontWeight.Normal,
                )
            },
            shape = RoundedCornerShape(16.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Send
            ),
            keyboardActions = KeyboardActions(
                onSend = {
                    sendMessage(
                        messageContext = messageContext,
                        userIDInfo = userIDInfo,
                        navBack = navBack,
                        viewModel = viewModel,
                        messageList = messageList
                    )
                }
            ),
            trailingIcon = {
                IconButton(
                    onClick = {
                        sendMessage(
                            messageContext = messageContext,
                            userIDInfo = userIDInfo,
                            navBack = navBack,
                            viewModel = viewModel,
                            messageList = messageList
                        )
                    },
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.Send,
                        contentDescription = "Send Message"
                    )
                }
            },
        )
    }
}

private fun sendMessage(
    messageContext: MutableState<String>,
    userIDInfo: String?,
    navBack: NavBackStackEntry,
    viewModel: MessageViewModel,
    messageList: MutableState<List<Message>?>
) {
    val receiverID = navBack.arguments?.getString("receiverID")
    val itemID = navBack.arguments?.getString("itemID")
    val conversationUserID = navBack.arguments?.getString("conversationUserID")

    if (userIDInfo != null && receiverID != null && itemID != null && conversationUserID != null) {
        val messageToSend = messageContext.value.trim()
        if (messageToSend.isNotEmpty()) {
            val newMessage = Message(
                message = messageToSend,
                senderID = userIDInfo,
                timestamp = Timestamp.now() // Replace with actual timestamp if available
            )
            messageList.value = messageList.value.orEmpty().plus(newMessage)

            if (!messageList.value.isNullOrEmpty()) {
                viewModel.sendMessage(
                    userID = userIDInfo,
                    conversationUserID = conversationUserID,
                    itemID = itemID,
                    messageContext = messageToSend
                )
            } else {
                viewModel.sendFirstMessage(
                    userID = userIDInfo,
                    conversationUserID = conversationUserID,
                    itemID = itemID,
                    messageContext = messageToSend
                )
            }
            messageContext.value = ""
        }
    }
}


private fun List<Message>.groupByDay(): Map<Date, List<Message>> {
    val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
    return this.groupBy {
        val calendar = Calendar.getInstance()
        calendar.time = it.timestamp.toDate()
        dateFormat.format(calendar.time)
    }.mapKeys {
        dateFormat.parse(it.key)!!
    }.toSortedMap(compareBy { it.time })
}
