package com.android.burakgunduz.bitirmeprojesi.ui.screens.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.burakgunduz.bitirmeprojesi.R
import com.android.burakgunduz.bitirmeprojesi.ui.theme.fonts.archivoFonts
import com.android.burakgunduz.bitirmeprojesi.viewModels.Item
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun ItemLocationMap(
    itemDetailsFor: Item,
    context: Context,
    isDarkModeOn: Boolean
) {
    val cameraPositionState: CameraPositionState = rememberCameraPositionState {
        position = CameraPosition.builder()
            .target(LatLng(itemDetailsFor.itemLocation.latitude, itemDetailsFor.itemLocation.longitude))
            .zoom(16f)
            .build()
    }
    val latLng = remember { mutableStateOf(itemDetailsFor.itemLocation) }
    val isLoading = remember { mutableStateOf(true) }

    val style = MapStyleOptions.loadRawResourceStyle(context, R.raw.dark_mode)

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Location",
            fontFamily = archivoFonts,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = (-2).sp,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(10.dp)
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!isLoading.value) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(360.dp, 280.dp)
                        .padding(10.dp)
                )
            } else {
                GoogleMap(
                    modifier = Modifier
                        .size(360.dp, 280.dp)
                        .clip(AbsoluteRoundedCornerShape(10.dp)),
                    cameraPositionState = cameraPositionState,
                    onMapClick = {
                        // Create an Intent to open Google Maps at a specific latitude and longitude
                        val latLong = itemDetailsFor.itemLocation // Assuming this is a LatLng object
                        val gmmIntentUri = Uri.parse("geo:${latLong.latitude},${latLong.longitude}?q=${latLong.latitude},${latLong.longitude}")
                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
                            setPackage("com.google.android.apps.maps")
                        }
                       if (mapIntent.resolveActivity(context.packageManager) != null) {
                            context.startActivity(mapIntent)
                        }
                    },
                    properties = if (isDarkModeOn) MapProperties(mapStyleOptions = style) else MapProperties()
                ) {
                    if (latLng.value.latitude != 0.0 && latLng.value.longitude != 0.0) {
                        Circle(
                            center = LatLng(latLng.value.latitude,latLng.value.longitude), // Set your circle center
                            radius = (150.0),  // Set your circle radius
                            fillColor = (Color(0x220000FF)),
                            strokeColor = (Color(0x220000FF)),
                            strokeWidth = (10f)
                        )
                    }
                }
            }
        }
    }
}

