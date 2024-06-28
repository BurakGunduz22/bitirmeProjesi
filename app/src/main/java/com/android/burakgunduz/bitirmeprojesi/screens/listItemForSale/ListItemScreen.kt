package com.android.burakgunduz.bitirmeprojesi.screens.listItemForSale

import android.net.Uri
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.android.burakgunduz.bitirmeprojesi.screens.listItemForSale.subScreens.ListItemDetails
import com.android.burakgunduz.bitirmeprojesi.screens.listItemForSale.subScreens.ListItemLocation
import com.android.burakgunduz.bitirmeprojesi.screens.listItemForSale.subScreens.UploadItemImages
import com.android.burakgunduz.bitirmeprojesi.viewModels.ItemViewModel
import com.android.burakgunduz.bitirmeprojesi.viewModels.LocationViewModel

@Composable
fun ListItemScreen(
    itemViewModel: ItemViewModel,
    locationViewModel: LocationViewModel,
    userID: String,
    navigatorController: NavController,
    isDarModeOn: Boolean
) {
    val countryNames = locationViewModel.countryNames.observeAsState()
    val screenNumber = remember { mutableIntStateOf(0) }
    val emptyUri = Uri.EMPTY
    val images = remember { MutableList(6) { emptyUri } }
    val coroutineScope = rememberCoroutineScope()
    val item = remember { MutableList(7) { "" } }
    val itemLocation = remember { MutableList(5) { "" } }
    val nextScreen = remember { mutableStateOf(false) }
    Surface(modifier = Modifier.fillMaxSize()) {
        when (screenNumber.intValue) {
            0 -> UploadItemImages(
                images = images,
                coroutineScope = coroutineScope,
                screenNumber = screenNumber,
                countryNames = countryNames,
                locationViewModel = locationViewModel,
                navigatorController = navigatorController,
                nextScreen = nextScreen
            )

            1 -> ListItemDetails(
                itemDetails = item,
                screenNumber = screenNumber,
                coroutineScope = coroutineScope,
                nextScreen = nextScreen,
                itemViewModel = itemViewModel
            )

            2 -> ListItemLocation(
                itemDetails = item,
                itemLocation = itemLocation,
                images = images,
                viewModel = itemViewModel,
                locationViewModel = locationViewModel,
                coroutineScope = coroutineScope,
                countryNames = countryNames,
                userID = userID,
                screenNumber = screenNumber,
                nextScreen = nextScreen,
                isDarkModeOn = isDarModeOn
            )
        }

    }
}
