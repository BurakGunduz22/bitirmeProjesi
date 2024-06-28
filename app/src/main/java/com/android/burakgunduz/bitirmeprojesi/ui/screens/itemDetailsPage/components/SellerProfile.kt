package com.android.burakgunduz.bitirmeprojesi.ui.screens.itemDetailsPage.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.android.burakgunduz.bitirmeprojesi.ui.theme.fonts.archivoFonts
import com.android.burakgunduz.bitirmeprojesi.viewModels.ItemViewModel

@Composable
fun SellerProfile(
    sellerProfileName: String,
    navController: NavController,
    sellerID: String,
    itemViewModel: ItemViewModel,
) {
    val userImage = itemViewModel.sellerImage.observeAsState()
    LaunchedEffect(Unit) {
        itemViewModel.getSellerProfile(sellerID)
    }
    Row(
        modifier = Modifier
            .width(200.dp)
            .padding(5.dp)
            .clip(AbsoluteRoundedCornerShape(25))

            .clickable {
                navController.navigate("sellerProfileScreen/$sellerID")
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        SubcomposeAsyncImage(
            model = userImage.value,
            contentDescription = "ProfilePicture",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .padding(10.dp)
                .size(25.dp)
                .clip(shape = CircleShape)
                .border(1.dp, MaterialTheme.colorScheme.onSurface, CircleShape)

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
            text = sellerProfileName,
            fontFamily = archivoFonts,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 18.sp,
            letterSpacing = (-2).sp,
            style = MaterialTheme.typography.titleLarge,
            textDecoration = TextDecoration.Underline
        )
    }
}