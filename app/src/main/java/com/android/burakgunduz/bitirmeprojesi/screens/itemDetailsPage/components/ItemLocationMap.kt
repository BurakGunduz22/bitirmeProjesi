package com.android.burakgunduz.bitirmeprojesi.screens.itemDetailsPage.components

import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.android.burakgunduz.bitirmeprojesi.ui.theme.fonts.archivoFonts
import com.android.burakgunduz.bitirmeprojesi.ViewModels.Item
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState

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
