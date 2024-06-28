package com.android.burakgunduz.bitirmeprojesi.ui.screens.listItemForSale.subScreens

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBackIos
import androidx.compose.material.icons.automirrored.outlined.ArrowForwardIos
import androidx.compose.material.icons.automirrored.outlined.NotListedLocation
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import com.android.burakgunduz.bitirmeprojesi.R
import com.android.burakgunduz.bitirmeprojesi.ui.screens.components.ExposedDropdownMenu
import com.android.burakgunduz.bitirmeprojesi.ui.screens.components.ProgressBar
import com.android.burakgunduz.bitirmeprojesi.ui.theme.fonts.archivoFonts
import com.android.burakgunduz.bitirmeprojesi.viewModels.CountryInfo
import com.android.burakgunduz.bitirmeprojesi.viewModels.ItemViewModel
import com.android.burakgunduz.bitirmeprojesi.viewModels.LocationViewModel
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.tasks.Task
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@SuppressLint("MutableCollectionMutableState", "CoroutineCreationDuringComposition")
@Composable
fun ListItemLocation(
    itemDetails: MutableList<String>,
    images: MutableList<Uri>,
    viewModel: ItemViewModel,
    locationViewModel: LocationViewModel,
    coroutineScope: CoroutineScope,
    countryNames: State<List<CountryInfo>?>,
    userID: String,
    screenNumber: MutableState<Int>,
    itemLocation: MutableList<String>,
    nextScreen: MutableState<Boolean>,
    isDarkModeOn: Boolean
) {
    val country = remember { mutableStateOf(itemLocation.getOrNull(0) ?: "") }
    val city = remember { mutableStateOf(itemLocation.getOrNull(1) ?: "") }
    val town = remember { mutableStateOf(itemLocation.getOrNull(2) ?: "") }
    val district = remember { mutableStateOf(itemLocation.getOrNull(3) ?: "") }
    val street = remember { mutableStateOf(itemLocation.getOrNull(4) ?: "") }
    val location = remember { mutableStateOf("") }
    val cityNames =
        locationViewModel.cityNames.observeAsState().value?.toMutableList() ?: mutableListOf()
    val townNames = locationViewModel.townNames.observeAsState().value ?: listOf()
    val countryList = countryNames.value?.map { it } ?: listOf()
    val localContext = LocalContext.current
    BackHandler {
        coroutineScope.launch {
            screenNumber.value = 1
            itemLocation.clear()
            itemLocation.addAll(
                listOf(
                    country.value,
                    city.value,
                    town.value,
                    district.value,
                    street.value
                )
            )
        }
    }
    val latLng = remember { mutableStateOf(LatLng(39.925533, 32.866287)) }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            fetchLocation(localContext) {
                location.value = it
                val geocoder = Geocoder(localContext)
                val address = geocoder.getFromLocationName(location.value, 1)
                if (address != null && address.size > 0) {
                    val location = address[0]
                    latLng.value = LatLng(location.latitude, location.longitude)
                }
                Log.e("Location", location.value)
                val locationParts = location.value.split(",")
                Log.e("Location", locationParts.toString())
                val cityParts = locationParts[2].split(" ")
                Log.e("Location", cityParts.toString())
                val townParts= cityParts[3].split("/")
                Log.e("Location", townParts.toString())
                val streetParts = locationParts[1].split("Sk.")
                Log.e("Location", streetParts.toString())
                    street.value = streetParts[0].trim()
                    district.value = locationParts[0].trim()
                    town.value = townParts[0].trim()
                    city.value = cityParts[2].trim()
                    country.value = locationParts[3].trim()
            }
        }
    }
    val countryValue = country.value
    val cityValue = city.value
    val townValue = town.value
    LaunchedEffect(nextScreen) {
        delay(500)
        nextScreen.value = true
    }
    Log.e("LogDetails", itemDetails.toString())
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedVisibility(visible = nextScreen.value) {
            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxHeight(0.8f)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Add Location Detail",
                    style = MaterialTheme.typography.headlineLarge,
                    fontFamily = archivoFonts,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-1).sp,
                    fontSize = 30.sp,
                )
                val cameraPositionState: CameraPositionState = rememberCameraPositionState {
                    position = CameraPosition.builder()
                        .target(latLng.value)
                        .zoom(17f)
                        .build()
                }
                val style = MapStyleOptions.loadRawResourceStyle(localContext, R.raw.dark_mode)
                GoogleMap(
                    modifier = Modifier
                        .size(360.dp, 280.dp)
                        .clip(AbsoluteRoundedCornerShape(10.dp)),
                    cameraPositionState = cameraPositionState,
                    properties = if (isDarkModeOn) MapProperties(mapStyleOptions = style) else MapProperties()

                ) {
                    Circle(
                        center = latLng.value, // Set your circle center
                        radius = (150.0),  // Set your circle radius
                        fillColor = (Color(0x220000FF)),
                        strokeColor = (Color(0x220000FF)),
                        strokeWidth = (10f)
                    )
                }
                Column(
                    modifier = Modifier.fillMaxHeight(0.8f),
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    ExposedDropdownMenu(
                        updatedData = country,
                        items = countryList,
                        isLocationFilled = !countryList.isNullOrEmpty(),
                        nameOfDropBox = "Country",
                        locationString = countryValue
                    )
                    LaunchedEffect(country.value) {
                        coroutineScope.launch {
                            countryNames.value?.let {
                                locationViewModel.fetchCitiesOfCountry(
                                    country.value, it
                                )
                            }
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(0.9f),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ExposedDropdownMenu(
                            updatedData = city,
                            items = cityNames,
                            isLocationFilled = true,
                            nameOfDropBox = "City",
                            modifier = Modifier.width(150.dp),
                            locationString = cityValue
                        )
                        LaunchedEffect(city.value) {
                            coroutineScope.launch {
                                cityNames.let {
                                    locationViewModel.fetchTownOfCity(
                                        town.value, it
                                    )
                                }
                            }
                        }
                        ExposedDropdownMenu(
                            updatedData = town,
                            items = townNames,
                            isLocationFilled = true,
                            nameOfDropBox = "Town",
                            locationString = townValue,
                            modifier = Modifier.width(150.dp)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(0.9f),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = district.value,
                            onValueChange = { district.value = it },
                            shape = AbsoluteRoundedCornerShape(16),
                            label = { Text("District") },
                            modifier = Modifier.width(150.dp),
                            maxLines = 1
                        )
                        OutlinedTextField(
                            value = street.value,
                            onValueChange = {
                                street.value = it
                            },
                            shape = AbsoluteRoundedCornerShape(16),
                            label = { Text("Street") },
                            modifier = Modifier.width(150.dp),
                            maxLines = 1
                        )
                    }
                }
            }
        }
        val isMet =
            country.value.isNotEmpty() && city.value.isNotEmpty() && town.value.isNotEmpty() && district.value.isNotEmpty() && street.value.isNotEmpty()
        ProgressBar(
            isProgressRequirementsMet = isMet,
            currentIcon = Icons.AutoMirrored.Outlined.NotListedLocation,
            nextIcon = Icons.AutoMirrored.Outlined.ArrowForwardIos,
            previousIcon = Icons.AutoMirrored.Outlined.ArrowBackIos,
            nextScreen = {
                itemDetails.addAll(
                    listOf(
                        country.value,
                        city.value,
                        town.value,
                        district.value,
                        street.value
                    )
                )
                itemDetails.forEach {
                    Log.d("ItemDetails", it)
                }
                viewModel.saveItem(images, itemDetails, userID)
            },
            previousScreen = {
                screenNumber.value = 1
                itemLocation.clear()
                itemLocation.addAll(
                    listOf(
                        country.value,
                        city.value,
                        town.value,
                        district.value,
                        street.value
                    )
                )
                nextScreen.value = false
            })
    }
}


@Composable
fun LocationButton(locationData: MutableState<String>) {
    var localContext = LocalContext.current

    val coroutineScope = rememberCoroutineScope()
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = "Location Data: ${locationData.value.split(",")}",
            modifier = Modifier.padding(top = 25.dp)
        )

        Button(
            onClick = {
                coroutineScope.launch {
                    fetchLocation(localContext) {
                        locationData.value = it
                        Log.e("Location", locationData.value)

                    }
                }

            },
        ) {
            Text("Get Location")
        }
    }
}

@SuppressLint("MissingPermission")
private suspend fun fetchLocation(localContext: Context, callback: (String) -> Unit): String {
    return suspendCancellableCoroutine { continuation ->
        val fusedLocationClient: FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(localContext)

        // Check if location services are enabled
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        val client: SettingsClient = LocationServices.getSettingsClient(localContext)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener { _ ->
            // All location settings are satisfied. Proceed with location request.
            if (ActivityCompat.checkSelfPermission(
                    localContext,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Request location permission
                ActivityCompat.requestPermissions(
                    localContext as Activity,
                    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                    1001 // Request code, handle onRequestPermissionsResult
                )
                return@addOnSuccessListener
            }

            // Location permission granted, fetch last known location
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val geocoder = Geocoder(localContext)
                    try {
                        val addresses =
                            geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        if (addresses != null) {
                            if (addresses.isNotEmpty()) {
                                continuation.resume(addresses[0].getAddressLine(0))
                                callback(addresses[0].getAddressLine(0).toString())
                            } else {
                                continuation.resume("Location: Unknown")
                            }
                        }
                    } catch (e: IOException) {
                        continuation.resumeWithException(e)
                        Log.e("Location", "Error: $e")
                    }
                } else {
                    continuation.resume("Location: Unknown")
                }
            }.addOnFailureListener { e ->
                continuation.resumeWithException(e)
                Log.e("Location", "Error: $e")
            }
        }.addOnFailureListener { e ->
            // Location settings are not satisfied, prompt user to enable location
            if (e is ResolvableApiException) {
                try {
                    // Prompt user to enable location services
                    e.startResolutionForResult(
                        localContext as Activity,
                        1002 // Request code for location resolution
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    Log.e("Location", "Error: ${sendEx.message}")
                    continuation.resumeWithException(sendEx)
                }
            } else {
                // Handle other failure cases
                Log.e("Location", "Error: $e")
                continuation.resumeWithException(e)
            }
        }
    }
}

