package com.android.burakgunduz.bitirmeprojesi.screens.listItemForSale

import android.app.Activity
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityOptionsCompat
import com.android.burakgunduz.bitirmeprojesi.screens.listItemForSale.components.PhotoCard
import com.android.burakgunduz.bitirmeprojesi.screens.listItemForSale.subScreens.ListItemDetails
import com.android.burakgunduz.bitirmeprojesi.screens.listItemForSale.subScreens.ListItemLocation
import com.android.burakgunduz.bitirmeprojesi.screens.listItemForSale.subScreens.UploadItemImages
import com.android.burakgunduz.bitirmeprojesi.viewModels.CountryInfo
import com.android.burakgunduz.bitirmeprojesi.viewModels.ItemViewModel
import com.android.burakgunduz.bitirmeprojesi.viewModels.LocationViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ListItemScreen(viewModel: ItemViewModel,locationViewModel: LocationViewModel) {
    val countryNames = locationViewModel.countryNames.observeAsState()
    val context = LocalContext.current
    val screenNumber = remember { mutableIntStateOf(0) }
    val images = remember { mutableStateOf(listOf<Uri>()) }
    val coroutineScope = rememberCoroutineScope()
    val item = remember { mutableListOf("") }
    Surface(modifier = Modifier.fillMaxSize()) {
        when (screenNumber.intValue) {
            0 -> UploadItemImages(
                images = images,
                coroutineScope = coroutineScope,
                screenNumber = screenNumber,
                countryNames = countryNames,
                locationViewModel = locationViewModel
            )

            1 -> ListItemDetails(
                itemDetails = item,
                screenNumber = screenNumber,
                countryNames = countryNames
            )

            2 -> ListItemLocation(
                itemDetails = item,
                images = images,
                screenNumber = screenNumber,
                viewModel = viewModel,
                countryNames = countryNames,
                coroutineScope = coroutineScope,
                locationViewModel = locationViewModel
            )
        }
    }
}

@Composable
fun PhotoSelection(
    images: MutableState<List<Uri>>,
    coroutineScope: CoroutineScope,
    screenNumber: MutableState<Int>,
    context: Context = LocalContext.current,
    countryNames: State<List<CountryInfo>?>,
    locationViewModel: LocationViewModel
) {
    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) {
            images.value = it
        }

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        if (images != null) {
            LazyVerticalGrid(columns = GridCells.Fixed(2)) {
                itemsIndexed(images.value) { index, item ->
                    PhotoCard(index = item, indexCount = index)
                }

            }
        }
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (images != null) {
                Button(onClick = {
                    screenNumber.value = 1
                }) {
                    Text("Next")
                }
            }
            Button(onClick = {
                coroutineScope.launch {
                    val activityOptionsCompat: ActivityOptionsCompat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                            context as Activity
                        )
                    galleryLauncher.launch("image/*", activityOptionsCompat)
                    if (countryNames.value == null) {
                        locationViewModel.fetchCountryNames()
                    }
                }
            }) {
                Text("Select Image")
            }
        }

    }
}