package com.android.burakgunduz.bitirmeprojesi.screens.itemDetailsPage.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.android.burakgunduz.bitirmeprojesi.ui.theme.colors.floatingLikeButtonContainer
import com.android.burakgunduz.bitirmeprojesi.ui.theme.colors.floatingLikeButtonContent
import com.android.burakgunduz.bitirmeprojesi.ui.theme.colors.successButtonColor
import com.android.burakgunduz.bitirmeprojesi.ui.theme.fonts.archivoFonts

@Composable
fun SendMessageToSellerButton(
    isDarkModeOn: Boolean,
    navController: NavController,
    itemUserID: String,
    itemID: String,
    currentUserID: String?,
    toggleButtonChecked: MutableState<Boolean>,
    favoriteAddAction: () -> Unit,
    favoriteRemoveAction: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            FloatingActionButton(
                onClick = { navController.navigate("directMessageToSeller/$currentUserID&$itemID&$itemUserID") },
                containerColor = successButtonColor(isDarkModeOn = isDarkModeOn).containerColor,
                contentColor = successButtonColor(isDarkModeOn = isDarkModeOn).contentColor,
                shape = AbsoluteRoundedCornerShape(7.dp),
                modifier = Modifier
                    .size(250.dp, 50.dp)
                    .padding(end = 15.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.AutoMirrored.Filled.Message,
                        contentDescription = "Message",
                        modifier = Modifier.padding(top = 5.dp)
                    )
                    Text(
                        text = "Send Message",
                        style = MaterialTheme.typography.titleMedium,
                        fontFamily = archivoFonts,
                        fontWeight = FontWeight.ExtraBold,
                    )
                }
            }
            FloatingActionButton(
                onClick = {
                    toggleButtonChecked.value = !toggleButtonChecked.value
                    if (!toggleButtonChecked.value) favoriteRemoveAction()
                    else favoriteAddAction()
                },
                containerColor = floatingLikeButtonContainer,
                contentColor = floatingLikeButtonContent,
                shape = AbsoluteRoundedCornerShape(7.dp),
                modifier = Modifier
                    .size(50.dp, 50.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (!toggleButtonChecked.value) Icons.Outlined.FavoriteBorder else Icons.Filled.Favorite,
                        contentDescription = "Favorite",
                    )
                }
            }

        }
    }
}
