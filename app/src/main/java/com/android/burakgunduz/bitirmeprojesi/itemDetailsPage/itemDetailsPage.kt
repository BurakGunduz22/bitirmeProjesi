package com.android.burakgunduz.bitirmeprojesi.itemDetailsPage

import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.net.Uri
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.delay


@Composable
fun ItemDetailsPage(
    backStackEntry: NavBackStackEntry,
    fireStoreRef: FirebaseFirestore,
    fireStorageRef: StorageReference,
    navController: NavController
) {
    val itemId = backStackEntry.arguments?.getString("itemId")?.replace("{", "")?.replace("}", "")
    val context = LocalContext.current
    val itemDetailsFor = remember { mutableStateOf<ItemDetails?>(null) }
    val isMapLoaded = remember { mutableStateOf(false) }
    val isMapLoadedAgain = remember { mutableStateOf(true) }
    val fireStorageInfo = fireStorageRef.child("itemImages/$itemId")
    val itemImagesLinks = remember { mutableListOf<NamedUri>() }
    LaunchedEffect(itemId) {
        fireStoreRef.collection("itemsOnSale").document(itemId!!)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    itemDetailsFor.value = document.toObject(ItemDetails::class.java)
                } else {
                    println("No such document")
                }
            }
            .addOnFailureListener { exception ->
                println("Error getting document: $exception")
            }
        fireStorageInfo.listAll().addOnSuccessListener { listResult ->
            listResult.items.forEach { item ->
                item.downloadUrl.addOnSuccessListener { uri ->
                    Log.e("URLS", "ItemDetailsPage: $uri")
                    itemImagesLinks.add(NamedUri(item.name.split(".").first(), uri))
                    itemImagesLinks.sortBy { it.name.toInt() }
                    Log.e("DIZI", "ItemDetailsPage: $itemImagesLinks")
                }
            }
        }
    }
    LaunchedEffect(itemDetailsFor.value) {
        if (itemDetailsFor.value != null) {
            Log.e("ItemDetailsPage", "ItemDetailsPage: ${itemDetailsFor.value}")
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
    if (itemDetailsFor.value != null) {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                ImagePager(
                    imageUris = itemImagesLinks,
                    navController,
                    isMapLoadedAgain
                )
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                ) {
                    Log.e("OMFG", "ItemDetailsPage: ${itemImagesLinks}")
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.Start
                    ) {
                        if (isMapLoaded.value) {
                            item {
                                ItemName(itemName = itemDetailsFor.value!!.itemName)
                            }
                            item {
                                ItemPrice(itemPrice = itemDetailsFor.value!!.itemPrice)
                            }
                            item {
                                ItemConditions(itemCondition = itemDetailsFor.value!!.itemCondition)
                            }
                            item {
                                ItemDescription(itemDesc = itemDetailsFor.value!!.itemDesc)
                            }
                            if (isMapLoadedAgain.value) {
                                item {
                                    ItemLocationMap(
                                        itemDetailsFor = itemDetailsFor,
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
        }
    } else {
        CircularProgressIndicator()
    }

}

data class NamedUri(val name: String, val uri: Uri)

data class ItemDetails(
    val itemName: String = "",
    val itemDesc: String = "",
    val itemPrice: Int = 0,
    val itemBrand: String = "",
    val itemCategory: String = "",
    val itemStreet: String = "",
    val itemDistrict: String = "",
    val itemTown: String = "",
    val itemCity: String = "",
    val itemCountry: String = "",
    val itemDate: Timestamp = Timestamp.now(),
    val itemCondition: Int = 0,
    val itemSubCategory: String = "",
    val userID: String = ""
    // Add other fields as needed
)

@Composable
fun ItemLocationMap(
    itemDetailsFor: MutableState<ItemDetails?>,
    context: Context,
) {
    // Create a MutableState<Boolean> variable
    val isMapReady = remember { mutableStateOf(false) }

    val itemLocation = listOf(
        itemDetailsFor.value?.itemStreet ?: "",
        itemDetailsFor.value?.itemDistrict ?: "",
        itemDetailsFor.value?.itemTown ?: "",
        itemDetailsFor.value?.itemCity ?: "",
        itemDetailsFor.value?.itemCountry ?: ""
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
        if (addresses != null && addresses.isNotEmpty()) {
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
    val imageLinks = imageUris.map { it.uri }
    val isTouched = remember { mutableStateOf(false) }
    val pagerState = rememberPagerState(pageCount = { imageLinks.size })
    val scale = remember { mutableStateOf(1f) }
    val zoomLevels = listOf(1f, 3f)
    val isZooming = remember { mutableStateOf(false) }
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
                .graphicsLayer(scaleX = scale.value, scaleY = scale.value)
                .transformable(state = rememberTransformableState { zoomChange, _, _ ->
                    val newScale = scale.value * zoomChange
                    if (newScale in zoomLevels[0]..zoomLevels[1]) {
                        scale.value = newScale
                    }
                }
                )
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {

                        },
                        onDoubleTap = {
                            scale.value =
                                if (scale.value == zoomLevels[0]) zoomLevels[1] else zoomLevels[0]
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