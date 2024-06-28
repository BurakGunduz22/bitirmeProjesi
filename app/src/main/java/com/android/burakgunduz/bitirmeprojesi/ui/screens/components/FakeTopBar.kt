package com.android.burakgunduz.bitirmeprojesi.ui.screens.components

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.android.burakgunduz.bitirmeprojesi.ui.theme.fonts.archivoFonts

@Composable
fun FakeTopBar(
    navController: NavController,
    screenName: String = "",
    isItDirectMessage: Boolean = false,
    userName: String = "",
    userImageUrl: Uri = Uri.EMPTY,
    sellerID: String = ""
) {
    if (isItDirectMessage) {
        Column(Modifier.fillMaxWidth()) {
            IconButton(onClick = {
                navController.navigateUp()
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Back"
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier
                        .width(250.dp)
                        .padding(5.dp)
                        .clip(AbsoluteRoundedCornerShape(25))
                        .clickable {
                            navController.navigate("sellerProfileScreen/$sellerID")
                        },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    SubcomposeAsyncImage(
                        model = userImageUrl,
                        contentDescription = "ProfilePicture",
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier
                            .padding(10.dp)
                            .size(25.dp)
                            .clip(shape = CircleShape)

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
                    Text(
                        text = userName,
                        fontFamily = archivoFonts,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 25.sp,
                        letterSpacing = (-2).sp,
                        style = MaterialTheme.typography.titleLarge,
                    )
                }
            }

        }

    } else {
        Column {
            IconButton(onClick = {
                navController.navigateUp()
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Back"
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = screenName,
                    fontFamily = archivoFonts,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 34.sp,
                    letterSpacing = (-2).sp,
                    style = MaterialTheme.typography.titleLarge,
                )
            }
        }
    }
}