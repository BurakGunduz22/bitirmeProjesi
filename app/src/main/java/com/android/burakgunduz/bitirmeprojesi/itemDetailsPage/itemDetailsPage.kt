package com.android.burakgunduz.bitirmeprojesi.itemDetailsPage

import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.net.Uri
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Close
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import com.android.burakgunduz.bitirmeprojesi.fonts.archivoFonts
import com.android.burakgunduz.bitirmeprojesi.itemViewModel.Item
import com.android.burakgunduz.bitirmeprojesi.itemViewModel.ItemViewModel
import com.android.burakgunduz.bitirmeprojesi.itemViewModel.NamedUri
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.delay


@Composable
fun ItemDetailsPage(
    backStackEntry: NavBackStackEntry,
    navController: NavController,
    viewModel: ItemViewModel
) {
    val itemDetailsViewModel = viewModel.itemDetails.value
    val itemImages = viewModel.itemImages.value
    val itemId = backStackEntry.arguments?.getString("itemId")?.replace("{", "")?.replace("}", "")
    val context = LocalContext.current
    val isMapLoaded = remember { mutableStateOf(false) }
    val isMapLoadedAgain = remember { mutableStateOf(true) }
    LaunchedEffect(itemId) {
        if (itemId != null) {
            viewModel.loadItemDetails(itemId)
            viewModel.loadItemImages(itemId)
        }
    }

    LaunchedEffect(isMapLoaded.value) {
        if (!isMapLoaded.value) {
            delay(500) // delay for 3 seconds
            isMapLoaded.value = true
        }
    }
    LaunchedEffect(isMapLoadedAgain.value) {
        if (!isMapLoadedAgain.value) {
            delay(500) // delay for 3 seconds
            isMapLoadedAgain.value = true
        }
    }
    Surface(modifier = Modifier.fillMaxSize()) {
        if (itemDetailsViewModel != null) {
            Column(modifier = Modifier.fillMaxSize()) {
                ImagePager(
                    imageUris = itemImages!!,
                    navController,
                    isMapLoadedAgain
                )
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.Start
                    ) {
                        if (isMapLoaded.value) {
                            item {
                                ItemName(itemName = itemDetailsViewModel.itemName)
                            }
                            item {
                                ItemPrice(itemPrice = itemDetailsViewModel.itemPrice)
                            }
                            item {
                                ItemConditions(itemCondition = itemDetailsViewModel.itemCondition)
                            }
                            item {
                                ItemDescription(itemDesc = itemDetailsViewModel.itemDesc)
                            }
                            if (isMapLoadedAgain.value) {
                                item {
                                    ItemLocationMap(
                                        itemDetailsFor = itemDetailsViewModel,
                                        context = context
                                    )
                                }
                            }
                        } else {
                            item { LinearProgressIndicator() }
                        }
                    }
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize()) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .size(360.dp, 10.dp)
                        .align(Alignment.Center)
                )
            }
        }
    }

}


@Composable
fun ItemLocationMap(
    itemDetailsFor: Item,
    context: Context,
) {
    val itemLocation = listOf(
        itemDetailsFor.itemStreet,
        itemDetailsFor.itemDistrict,
        itemDetailsFor.itemTown,
        itemDetailsFor.itemCity,
        itemDetailsFor.itemCountry
    )
    val locationQuery =
        "${itemLocation[0]},${itemLocation[1]},${itemLocation[2]},${itemLocation[3]},${itemLocation[4]}"
    val geocoder = Geocoder(context)

    val cameraPositionState: CameraPositionState = rememberCameraPositionState {
        position = CameraPosition.builder()
            .target(LatLng(0.0, 0.0))
            .zoom(10f)
            .build()
    }

    val latLng = remember(itemLocation) { mutableStateOf(LatLng(0.0, 0.0)) }

    LaunchedEffect(locationQuery) {
        val addresses = geocoder.getFromLocationName(locationQuery, 1)
        if (!addresses.isNullOrEmpty()) {
            val address = addresses[0]
            if (address.hasLatitude() && address.hasLongitude()) {
                latLng.value = LatLng(address.latitude, address.longitude)
                cameraPositionState.position = CameraPosition.builder()
                    .target(latLng.value)
                    .zoom(17f)
                    .build()
            }
        }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Location",
            fontFamily = archivoFonts,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.titleLarge,
        )
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        GoogleMap(
            modifier = Modifier
                .size(360.dp, 280.dp)
                .clip(AbsoluteRoundedCornerShape(10.dp)),
            cameraPositionState = cameraPositionState,
            onMapClick = {
                // Create an Intent to open Google Maps with the search query
                val gmmIntentUri = Uri.parse("geo:0,0?q=$locationQuery")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")

                // Start the Intent
                if (mapIntent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(mapIntent)
                }
            },
        ) {
            Circle(
                center = latLng.value, // Set your circle center
                radius = (150.0),  // Set your circle radius
                fillColor = (Color(0x220000FF)),
                strokeColor = (Color(0x220000FF)),
                strokeWidth = (10f)
            )
        }
    }

}

@Composable
fun ItemConditions(itemCondition: Int) {
    return when (itemCondition) {
        0 -> Text(
            "New",
            style = MaterialTheme.typography.bodyMedium,
            fontFamily = archivoFonts,
            fontWeight = FontWeight.SemiBold,
            color = Color.Green,
            modifier = Modifier.padding(10.dp)
        )

        1 -> Text(
            "Used",
            style = MaterialTheme.typography.bodyMedium,
            fontFamily = archivoFonts,
            fontWeight = FontWeight.SemiBold,
            color = Color.Yellow,
            modifier = Modifier.padding(10.dp)
        )

        2 -> Text(
            "Refurbished",
            style = MaterialTheme.typography.bodyMedium,
            fontFamily = archivoFonts,
            fontWeight = FontWeight.SemiBold,
            color = Color.Yellow,
            modifier = Modifier.padding(10.dp)
        )

        else -> Text(
            "Unknown", style = MaterialTheme.typography.bodyMedium, fontFamily = archivoFonts,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
fun ItemDescription(itemDesc: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Description",
            fontFamily = archivoFonts,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.titleLarge,
        )
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, end = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = itemDesc,
            fontFamily = archivoFonts,
            fontWeight = FontWeight.Normal,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
fun ItemPrice(itemPrice: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$itemPrice €",
            fontFamily = archivoFonts,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.titleLarge,
        )
    }
}

@Composable
fun ItemName(itemName: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = itemName,
            fontFamily = archivoFonts,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.titleLarge,
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImagePager(
    imageUris: List<NamedUri>,
    navController: NavController,
    isMapLoaded: MutableState<Boolean>
) {
    val colorStops = arrayOf(
        0.0f to Color.hsl(0f, 0f, 0f, 0f),
        0.4f to Color.hsl(0f, 0f, 0.1f, 0.1f),
        1f to Color.Black.copy(alpha = 0.8f)
    )
    val colorStops2 = arrayOf(
        0.0f to Color.hsl(0f, 0f, 0f, 0f),
        0.0f to Color.hsl(0f, 0f, 0f, 0f)
    )
    val imageLinks = imageUris.map { it.uri }
    val isTouched = remember { mutableStateOf(false) }
    val pagerState = rememberPagerState(pageCount = { imageLinks.size })
    val scale = remember { mutableFloatStateOf(1f) }
    val zoomLevels = listOf(1f, 3f)
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    val callback = remember {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isTouched.value) {
                    isTouched.value = false
                    isMapLoaded.value = false
                } else {
                    navController.popBackStack()
                }
            }
        }
    }
    DisposableEffect(backDispatcher) {
        backDispatcher?.addCallback(callback)
        onDispose { callback.remove() }
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .fillMaxHeight(if (isTouched.value) 0.8f else 0.3f)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .matchParentSize()
                .graphicsLayer(scaleX = scale.floatValue, scaleY = scale.floatValue)
                .transformable(state = rememberTransformableState { zoomChange, _, _ ->
                    val newScale = scale.floatValue * zoomChange
                    if (newScale in zoomLevels[0]..zoomLevels[1]) {
                        scale.floatValue = newScale
                    }
                }
                )
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {

                        },
                        onDoubleTap = {
                            scale.floatValue =
                                if (scale.floatValue == zoomLevels[0]) zoomLevels[1] else zoomLevels[0]
                        }
                    )
                },
            verticalAlignment = Alignment.CenterVertically,
        ) { page ->
            val uri = imageLinks[page]
            SubcomposeAsyncImage(
                model = uri,
                contentDescription = "",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier

            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(colorStops = if (isTouched.value) colorStops2 else colorStops))
        ) {
            Row(
                Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(pagerState.pageCount) { iteration ->
                    val color =
                        if (pagerState.currentPage == iteration) Color.LightGray else Color.DarkGray
                    val size = if (pagerState.currentPage == iteration) 12.dp else 8.dp
                    Box(
                        modifier = Modifier
                            .padding(2.dp)
                            .clip(CircleShape)
                            .background(color)
                            .size(size)
                    )
                }
            }
        }
        if (isTouched.value) {
            IconButton(
                onClick = {
                    isTouched.value = false
                    isMapLoaded.value = false
                },
            ) {
                Icon(imageVector = Icons.Sharp.Close, contentDescription = "")
            }
        } else {
            Box(modifier = Modifier
                .matchParentSize()
                .clickable {
                    isTouched.value = true
                })
        }
    }
}