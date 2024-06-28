package com.android.burakgunduz.bitirmeprojesi.ui.screens.editItemScreen.subScreens

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.android.burakgunduz.bitirmeprojesi.ui.screens.components.ExposedDropdownMenu
import com.android.burakgunduz.bitirmeprojesi.viewModels.CountryInfo
import com.android.burakgunduz.bitirmeprojesi.viewModels.ItemViewModel
import com.android.burakgunduz.bitirmeprojesi.viewModels.LocationViewModel
import kotlinx.coroutines.CoroutineScope
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
    userID: String
) {
    val country = remember { mutableStateOf("") }
    val city = remember { mutableStateOf("") }
    val town = remember { mutableStateOf("") }
    val district = remember { mutableStateOf("") }
    val street = remember { mutableStateOf("") }
    val cityNames = locationViewModel.cityNames.observeAsState().value
    val townNames = locationViewModel.townNames.observeAsState().value
    val countryList = countryNames.value?.map { it }
    Log.e("LogDetails", itemDetails.toString())
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
        if (countryList != null) {
            ExposedDropdownMenu(
                updatedData = country,
                items = countryList,
                isLocationFilled = false,
                nameOfDropBox = "Country"
            )
            LaunchedEffect(country.value) {
                coroutineScope.launch {
                    locationViewModel.fetchCitiesOfCountry(
                        country.value, countryNames.value!!
                    )
                }
            }
            if (cityNames != null) {
                ExposedDropdownMenu(
                    updatedData = city,
                    items = cityNames,
                    isLocationFilled = false,
                    nameOfDropBox = "City"
                )
                LaunchedEffect(city.value) {
                    coroutineScope.launch {
                        locationViewModel.fetchTownOfCity(
                            city.value, cityNames
                        )
                    }
                }
            }
            if (townNames != null) {
                ExposedDropdownMenu(
                    updatedData = town,
                    items = townNames,
                    isLocationFilled = false,
                    nameOfDropBox = "Town"
                )
            }
        }
        OutlinedTextField(
            value = district.value,
            onValueChange = { district.value = it },
            label = { Text("District") }
        )
        OutlinedTextField(
            value = street.value,
            onValueChange = {
                street.value = it
            },
            label = { Text("Street") }
        )
        Button(onClick = {
            itemDetails.removeAt(0)
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

        }) {
            Text("Save")
        }
    }
}




