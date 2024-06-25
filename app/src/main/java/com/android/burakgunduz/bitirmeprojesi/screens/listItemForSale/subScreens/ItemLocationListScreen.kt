package com.android.burakgunduz.bitirmeprojesi.screens.listItemForSale.subScreens

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBackIos
import androidx.compose.material.icons.automirrored.outlined.ArrowForwardIos
import androidx.compose.material.icons.automirrored.outlined.NotListedLocation
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.android.burakgunduz.bitirmeprojesi.screens.listItemForSale.components.ExposedDropdownMenu
import com.android.burakgunduz.bitirmeprojesi.screens.listItemForSale.components.ProgressBar
import com.android.burakgunduz.bitirmeprojesi.ui.theme.fonts.archivoFonts
import com.android.burakgunduz.bitirmeprojesi.viewModels.CountryInfo
import com.android.burakgunduz.bitirmeprojesi.viewModels.ItemViewModel
import com.android.burakgunduz.bitirmeprojesi.viewModels.LocationViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
    nextScreen: MutableState<Boolean>
) {
    val country = remember { mutableStateOf(itemLocation.getOrNull(0) ?: "") }
    val city = remember { mutableStateOf(itemLocation.getOrNull(1) ?: "") }
    val town = remember { mutableStateOf(itemLocation.getOrNull(2) ?: "") }
    val district = remember { mutableStateOf(itemLocation.getOrNull(3) ?: "") }
    val street = remember { mutableStateOf(itemLocation.getOrNull(4) ?: "") }
    val cityNames =
        locationViewModel.cityNames.observeAsState().value?.toMutableList() ?: mutableListOf()
    val townNames = locationViewModel.townNames.observeAsState().value ?: listOf()
    val countryList = countryNames.value?.map { it } ?: listOf()
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
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Add Location Detail",
                    style = MaterialTheme.typography.headlineLarge,
                    fontFamily = archivoFonts,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-1).sp,
                    fontSize = 30.sp,
                )
                Column(
                    modifier = Modifier.fillMaxHeight(0.8f),
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ExposedDropdownMenu(
                        updatedData = country,
                        items = countryList,
                        isLocationFilled = !countryList.isNullOrEmpty(),
                        nameOfDropBox = "Country"
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
                    ExposedDropdownMenu(
                        updatedData = city,
                        items = cityNames,
                        isLocationFilled = !cityNames.isNullOrEmpty(),
                        nameOfDropBox = "City"
                    )
                    LaunchedEffect(city.value) {
                        coroutineScope.launch {
                            if (cityNames != null) {
                                locationViewModel.fetchTownOfCity(
                                    city.value, cityNames
                                )
                            }
                        }
                    }
                    ExposedDropdownMenu(
                        updatedData = town,
                        items = townNames,
                        isLocationFilled = !townNames.isNullOrEmpty(),
                        nameOfDropBox = "Town"
                    )
                    OutlinedTextField(
                        value = district.value,
                        onValueChange = { district.value = it },
                        shape = AbsoluteRoundedCornerShape(16),
                        label = { Text("District") }
                    )
                    OutlinedTextField(
                        value = street.value,
                        onValueChange = {
                            street.value = it
                        },
                        shape = AbsoluteRoundedCornerShape(16),
                        label = { Text("Street") }
                    )
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




