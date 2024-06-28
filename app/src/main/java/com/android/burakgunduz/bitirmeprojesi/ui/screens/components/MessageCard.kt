package com.android.burakgunduz.bitirmeprojesi.ui.screens.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.android.burakgunduz.bitirmeprojesi.ui.theme.colors.itemSubTitleTextColorChanger
import com.android.burakgunduz.bitirmeprojesi.ui.theme.colors.itemTitleTextColorChanger
import com.android.burakgunduz.bitirmeprojesi.ui.theme.fonts.robotoFonts
import com.android.burakgunduz.bitirmeprojesi.viewModels.Message
import com.android.burakgunduz.bitirmeprojesi.viewModels.MessageViewModel
import java.time.Duration
import java.time.LocalDateTime
import java.time.Period
import java.time.ZoneId
import java.util.Date

@Composable
fun MessageCard(
    messageCardValue: Message,
    imageUrl: String,
    isDarkModeOn: Boolean,
    navController: NavController,
    userIDInfo: String?,
    messageViewModel: MessageViewModel,
    itemName: String,
) {
    val colorStops = arrayOf(
        0.0f to Color.hsl(0f, 0f, 0f, 0f),
        0.4f to Color.hsl(0f, 0f, 0.1f, 0.1f),
        1f to Color.Black
    )
    val itemID = messageCardValue.itemID
    val userID = if (messageCardValue.senderID == userIDInfo) {
        messageCardValue.senderID
    } else {
        messageCardValue.receiverID
    }
    val conversationUserID = if (messageCardValue.senderID == userID) {
        messageCardValue.receiverID
    } else {
        messageCardValue.senderID
    }
    val messagerName = remember { mutableStateOf("") }
    messageViewModel.getUserName(conversationUserID) {
        messagerName.value = it
    }

    Log.e("MessageCard", "MessageCard: $userID $itemID $conversationUserID")
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth(0.95f)
            .size(200.dp, 150.dp)
            .padding(10.dp)
            .clip(shape = AbsoluteRoundedCornerShape(10))
            .clickable { navController.navigate("directMessageToSeller/$userID&$itemID&$conversationUserID&${messagerName.value}&$itemName") }
    ) {
        Box {
            if (imageUrl.isNotEmpty()) {
                SubcomposeAsyncImage(
                    model = imageUrl,
                    contentDescription = "Item Image",
                    contentScale = ContentScale.FillWidth,
                    alignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    val state = painter.state
                    when (state) {
                        is AsyncImagePainter.State.Loading -> {
                            SkeletonLoader(
                                modifier = Modifier
                                    .fillMaxWidth().fillMaxHeight()
                            )
                        }

                        is AsyncImagePainter.State.Empty, is AsyncImagePainter.State.Error -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Error,
                                    contentDescription = "Error Icon"
                                )
                            }
                        }

                        else -> {
                            SubcomposeAsyncImageContent()
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.verticalGradient(colorStops = colorStops))
                )
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.Start
                ) {
                    TitleText(titleName = itemName, isDarkModeOn = isDarkModeOn)
                    Row {
                        DateTitleText(
                            messageCardValue.timestamp.toDate(),
                            isDarkModeOn,
                            10
                        )
                        SubTitletext("•", isDarkModeOn, 5)
                        SubTitletext(
                            subTitleName = messagerName.value,
                            isDarkModeOn = isDarkModeOn,
                            paddingValue = 5
                        )
                    }
                    SubTitletext(messageCardValue.message, isDarkModeOn, 10)
                }

            } else {
                MessageCardSkeleton()
            }
        }
    }
}

@Composable
fun MessageCardSkeleton() {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth(0.95f)
            .size(200.dp, 150.dp)
            .padding(10.dp)
            .clip(shape = AbsoluteRoundedCornerShape(10))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            SkeletonLoader(
                modifier = Modifier
                    .height(150.dp)
                    .fillMaxWidth()
            )
            Column(modifier = Modifier.padding(5.dp)) {
                SkeletonLoader(
                    modifier = Modifier
                        .height(12.dp)
                        .fillMaxWidth(0.6f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    SkeletonLoader(
                        modifier = Modifier
                            .height(12.dp)
                            .fillMaxWidth(0.4f)
                    )
                    SkeletonLoader(
                        modifier = Modifier
                            .height(12.dp)
                            .fillMaxWidth(0.2f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                SkeletonLoader(
                    modifier = Modifier
                        .height(24.dp)
                        .fillMaxWidth(0.4f)
                )
            }
        }
    }
}

fun getTimePassed(timestamp: LocalDateTime): String {
    val now = LocalDateTime.now()
    val period = Period.between(timestamp.toLocalDate(), now.toLocalDate())
    val duration = Duration.between(timestamp, now)

    return when {
        period.years > 0 -> "${period.years} years ago"
        period.months > 0 -> "${period.months} months ago"
        period.days > 0 -> "${period.days} days ago"
        duration.toHours() > 0 -> "${duration.toHours()} hours ago"
        duration.toMinutes() > 0 -> "${duration.toMinutes()} minutes ago"
        else -> "${duration.seconds} seconds ago"
    }
}

@Composable
fun TitleText(titleName: String, isDarkModeOn: Boolean) {
    Text(
        text = titleName,
        fontSize = 20.sp,
        fontFamily = robotoFonts,
        fontWeight = FontWeight.Bold,
        color = itemTitleTextColorChanger(isDarkModeOn = isDarkModeOn),
        modifier = Modifier
            .padding(top = 60.dp, start = 10.dp)
            .width(250.dp),
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
    )
}

@Composable
fun SubTitletext(subTitleName: String, isDarkModeOn: Boolean, paddingValue: Int) {
    Text(
        text = subTitleName,
        fontSize = 15.sp,
        fontFamily = robotoFonts,
        fontWeight = FontWeight.Medium,
        color = itemSubTitleTextColorChanger(isDarkModeOn = isDarkModeOn),
        modifier = Modifier.padding(bottom = 25.dp, start = paddingValue.dp)
    )
}

@Composable
fun DateTitleText(dateValue: Date, isDarkModeOn: Boolean, paddingValue: Int) {
    val messageDateConverter =
        dateValue.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
    val messageDate = getTimePassed(messageDateConverter)
    Text(
        text = messageDate,
        fontSize = 15.sp,
        fontFamily = robotoFonts,
        fontWeight = FontWeight.Medium,
        color = itemSubTitleTextColorChanger(isDarkModeOn = isDarkModeOn),
        modifier = Modifier.padding(bottom = 25.dp, start = paddingValue.dp)
    )
}
