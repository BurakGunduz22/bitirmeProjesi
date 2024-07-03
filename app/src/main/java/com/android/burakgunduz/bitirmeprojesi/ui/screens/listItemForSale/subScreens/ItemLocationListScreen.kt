package com.android.burakgunduz.bitirmeprojesi.ui.screens.listItemForSale.subScreens

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBackIos
import androidx.compose.material.icons.automirrored.outlined.ArrowForwardIos
import androidx.compose.material.icons.automirrored.outlined.NotListedLocation
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
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
import androidx.navigation.NavController
import com.android.burakgunduz.bitirmeprojesi.R
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
import com.google.android.gms.maps.CameraUpdateFactory
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
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

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
    isDarkModeOn: Boolean,
    navController: NavController
) {
    val localContext = LocalContext.current
    BackHandler {
        coroutineScope.launch {
            screenNumber.value = 1
            nextScreen.value = false
        }
    }

    val latLng = remember { mutableStateOf<LatLng?>(null) }
    val isMet = remember { mutableStateOf(false) }
    val isLoading = remember { mutableStateOf(false) } // Loading state

    val cameraPositionState: CameraPositionState = rememberCameraPositionState {
        latLng.value?.let {
            position = CameraPosition.builder()
                .target(it)
                .zoom(17f)
                .build()
        } ?: run {
            position = CameraPosition.builder()
                .target(LatLng(39.925533, 32.866287))  // Default position
                .zoom(17f)
                .build()
        }
    }

    // Fetch location on initialization
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            fetchLocation(localContext) { location ->
                latLng.value = location
                isMet.value = true
                // Update the camera position to the new location
                cameraPositionState.position = CameraPosition.builder()
                    .target(location)
                    .zoom(17f)
                    .build()
            }
        }
    }

    // Manage the visibility of the next screen after a delay
    LaunchedEffect(nextScreen) {
        delay(500)
        nextScreen.value = true
    }

    Log.e("LogDetails", itemDetails.toString())

    // Update the camera position when latLng.value changes
    LaunchedEffect(latLng.value) {
        latLng.value?.let {
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(it, 17f),
                1000 // Duration of the animation in milliseconds
            )
        }
    }

    val style = MapStyleOptions.loadRawResourceStyle(localContext, R.raw.dark_mode)

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
                GoogleMap(
                    modifier = Modifier
                        .size(360.dp, 280.dp)
                        .clip(AbsoluteRoundedCornerShape(10.dp)),
                    cameraPositionState = cameraPositionState,
                    properties = if (isDarkModeOn) MapProperties(mapStyleOptions = style) else MapProperties()
                ) {
                    latLng.value?.let {
                        Circle(
                            center = it, // Set your circle center
                            radius = (150.0),  // Set your circle radius
                            fillColor = (Color(0x220000FF)),
                            strokeColor = (Color(0x220000FF)),
                            strokeWidth = (10f)
                        )
                    }
                }
                LocationButton(latLng, isMet)
            }
        }

        if (isLoading.value) {
            CircularProgressIndicator() // Show loading spinner
        }

        ProgressBar(
            isProgressRequirementsMet = isMet.value,
            currentIcon = Icons.AutoMirrored.Outlined.NotListedLocation,
            nextIcon = Icons.AutoMirrored.Outlined.ArrowForwardIos,
            previousIcon = Icons.AutoMirrored.Outlined.ArrowBackIos,
            nextScreen = {
                latLng.value?.let {
                    itemDetails.add(it.latitude.toString())
                    itemDetails.add(it.longitude.toString())
                }
                isLoading.value = true // Show loading spinner
                viewModel.saveItem(images, itemDetails, userID) {
                    isLoading.value = false // Hide loading spinner
                    navController.navigate("feedScreenNav/${userID}")
                }
            },
            previousScreen = {
                screenNumber.value = 1
                nextScreen.value = false
            }
        )
    }
}


@Composable
fun LocationButton(
    locationData: MutableState<LatLng?>,
    isMet: MutableState<Boolean>
) {
    val localContext = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Button(
            onClick = {
                coroutineScope.launch {
                    fetchLocation(localContext) { location ->
                        locationData.value = location
                        isMet.value = true
                    }
                }
            },
            enabled = !isMet.value // Disable the button if location is already fetched
        ) {
            Text("Get Location")
        }
    }
}

@SuppressLint("MissingPermission")
private suspend fun fetchLocation(localContext: Context, callback: (LatLng) -> Unit): String {
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
                    callback(LatLng(location.latitude, location.longitude))
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



