package com.android.burakgunduz.bitirmeprojesi.screens.itemDetailsPage.components

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
    itemViewModel: ItemViewModel
) {
    val userImage = itemViewModel.sellerImage.observeAsState()
    LaunchedEffect(Unit) {
        itemViewModel.getSellerProfile(sellerID)
    }
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
        Button(
            onClick = {
                navController.navigate("sellerProfileScreen/$sellerID")
                Log.e("SellerProfile", sellerID)
            },
            modifier = Modifier.padding(10.dp),
            shape = RoundedCornerShape(7.dp)
        ) {
            SubcomposeAsyncImage(
                model = userImage.value,
                contentDescription = "ProfilePicture",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .size(30.dp)
                    .clip(shape = CircleShape)
            ) {
                val state = painter.state
                when (state) {
                    is AsyncImagePainter.State.Loading -> {
                        CircularProgressIndicator()
                    }

                    is AsyncImagePainter.State.Empty, is AsyncImagePainter.State.Error -> {
                        Icon(imageVector = Icons.Outlined.AccountCircle, contentDescription = "Person")
                    }

                    else -> {
                        SubcomposeAsyncImageContent()
                    }
                }
            }
            Text(
                text = sellerProfileName,
                style = MaterialTheme.typography.titleMedium,
                fontFamily = archivoFonts,
                fontWeight = FontWeight.ExtraBold,
            )
        }
    }
}