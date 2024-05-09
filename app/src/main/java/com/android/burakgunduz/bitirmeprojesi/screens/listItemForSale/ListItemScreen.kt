package com.android.burakgunduz.bitirmeprojesi.screens.listItemForSale

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityOptionsCompat
import com.android.burakgunduz.bitirmeprojesi.ViewModels.ItemViewModel
import com.android.burakgunduz.bitirmeprojesi.screens.listItemForSale.photoCard.PhotoCard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

@Composable
fun ListItemScreen(viewModel: ItemViewModel) {
    val context = LocalContext.current
    val screenNumber = remember { mutableIntStateOf(0) }
    val images = remember { mutableStateOf(listOf<Uri>()) }
    val coroutineScope = rememberCoroutineScope()
    val item = remember { mutableListOf("") }
    val countryNames = remember {
        mutableListOf("")
    }
    Surface(modifier = Modifier.fillMaxSize()) {
        when (screenNumber.intValue) {
            0 -> PhotoSelection(
                images = images,
                coroutineScope = coroutineScope,
                screenNumber = screenNumber,
                countryNames = countryNames
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
                countryNames = countryNames
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
    countryNames: MutableList<String>
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
                    fetchCountryNames(countryNames, 0)
                    fetchCountryNames(countryNames, 100)
                }
            }) {
                Text("Select Image")
            }
        }

    }
}

@Composable
fun ListItemDetails(
    itemDetails: MutableList<String>,
    screenNumber: MutableState<Int>,
    countryNames: MutableList<String>
) {
    val dropControl = remember { mutableStateOf(false) }
    val title = remember { mutableStateOf("") }
    val brand = remember { mutableStateOf("") }
    val itemCategory = remember { mutableStateOf("") }
    val description = remember { mutableStateOf("") }
    val price = remember { mutableStateOf("") }
    val condition = remember { mutableStateOf("") }
    countryNames.sort()
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
        Log.e("Filtered", countryNames.size.toString())
        ExposedDropdownMenu(
            items = countryNames,
        ) {
            condition.value = it
        }
        OutlinedTextField(value = title.value, onValueChange = {
            title.value = it
        }, label = {
            Text("Title")
        })
        OutlinedTextField(value = brand.value, onValueChange = {
            brand.value = it
        }, label = { Text("Brand") })
        OutlinedTextField(value = itemCategory.value, onValueChange = {
            itemCategory.value = it
        }, label = { Text("Item Category") })
        OutlinedTextField(
            value = description.value,
            onValueChange = {
                description.value = it
            },
            label = { Text("Description") }
        )
        OutlinedTextField(value = price.value, onValueChange = {
            price.value = it
        }, label = { Text("Price") })
        OutlinedCard(onClick = { dropControl.value = true }) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .wrapContentWidth()
                    .height(50.dp)
                    .padding(5.dp)
            ) {
                Text(
                    text = when (condition.value) {
                        0.toString() -> "New"
                        1.toString() -> "Used"
                        2.toString() -> "Refurbished"
                        else -> "Select Condition"
                    }
                )
            }
            DropdownMenu(
                expanded = dropControl.value,
                onDismissRequest = { dropControl.value = false }) {
                DropdownMenuItem(
                    text = { Text("New") },
                    onClick = {
                        condition.value = 0.toString()
                        dropControl.value = false
                    })
                DropdownMenuItem(
                    text = { Text("Used") },
                    onClick = {
                        condition.value = 1.toString()
                        dropControl.value = false
                    })
                DropdownMenuItem(
                    text = { Text("Refurbished") },
                    onClick = {
                        condition.value = 2.toString()
                        dropControl.value = false
                    })
            }
        }
        Button(onClick = {
            screenNumber.value = 2
            itemDetails.addAll(
                listOf(
                    title.value,
                    brand.value,
                    itemCategory.value,
                    description.value,
                    price.value,
                    condition.value
                )
            )
            itemDetails.forEach() {
                Log.d("ItemDetails", it)
            }
        }) {
            Text("Next")
        }
    }
}

@SuppressLint("MutableCollectionMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListItemLocation(
    itemDetails: MutableList<String>,
    screenNumber: MutableState<Int>,
    images: MutableState<List<Uri>>,
    viewModel: ItemViewModel,
    countryNames: MutableList<String>
) {
    val country = remember { mutableStateOf("") }
    val city = remember { mutableStateOf("") }
    val town = remember { mutableStateOf("") }
    val district = remember { mutableStateOf("") }
    val street = remember { mutableStateOf("") }
    val expanded = remember { mutableStateOf(false) }
    val filteredCountryNames = remember { mutableStateOf(countryNames) }
    val options = listOf("Option 1", "Option 2", "Option 3", "Option 4", "Option 5")
    var selectedOptionText by remember { mutableStateOf("") }
    var expandedKeko by remember { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
        ExposedDropdownMenuBox(
            expanded = expandedKeko,
            onExpandedChange = { expandedKeko = it },
        ) {
            TextField(
                value = selectedOptionText,
                onValueChange = { selectedOptionText = it },
                label = { Text("Label") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = expandedKeko
                    )
                },
                colors = ExposedDropdownMenuDefaults.textFieldColors()
            )
            // filter options based on text field value
            val filteringOptions =
                options.filter { it.contains(selectedOptionText, ignoreCase = true) }
            if (filteringOptions.isNotEmpty()) {
                ExposedDropdownMenu(
                    expanded = expandedKeko,
                    onDismissRequest = {
                        expandedKeko = false
                    }
                ) {
                    filteringOptions.forEach { selectionOption ->
                        DropdownMenuItem(
                            onClick = {
                                selectedOptionText = selectionOption
                                expandedKeko = false
                            },
                            text = { Text(text = selectionOption) }
                        )
                    }
                }
            }
        }
        ExposedDropdownMenuBox(
            expanded = expanded.value,
            onExpandedChange = { expanded.value = it },
        ) {
            OutlinedTextField(
                value = country.value,
                onValueChange = {
                    country.value = it
                    expanded.value = true
                },
                label = {
                    Text("Country")
                },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = expanded.value
                    )
                }
            )
            val filteringOptions =
                filteredCountryNames.value.filter { it.contains(country.value, ignoreCase = true) }
            ExposedDropdownMenu(
                expanded = expanded.value,
                onDismissRequest = { expanded.value = false }) {
                filteringOptions.forEach { name ->
                    DropdownMenuItem(onClick = {
                        country.value = name
                        expanded.value = false
                    }, text = { Text(name) })
                }
            }
        }

        OutlinedTextField(value = city.value, onValueChange = {
            city.value = it
        }, label = { Text("City") })
        OutlinedTextField(value = town.value, onValueChange = {
            town.value = it
        }, label = { Text("Town") })
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
            itemDetails.addAll(
                listOf(
                    country.value,
                    city.value,
                    town.value,
                    district.value,
                    street.value
                )
            )
            itemDetails.forEach() {
                Log.d("ItemDetails", it)
            }
            viewModel.saveItem(images, itemDetails)

        }) {
            Text("Save")
        }
    }
}

suspend fun fetchCountryNames(countryNames: MutableList<String>, startRow: Int) =
    withContext(Dispatchers.IO) {
        val urlString =
            "http://api.geonames.org/search?username=burakgunduz22&featureCode=PCLI&type=json&startRow=$startRow"

        val url = URL(urlString)
        val connection = url.openConnection() as HttpURLConnection
        try {
            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            val response = reader.readText()
            reader.close()

            // Parse the JSON response to get the country names
            val jsonObject = JSONObject(response)

            if (jsonObject.has("geonames")) {
                val geonamesArray = jsonObject.getJSONArray("geonames")
                countryNames.removeAt(0)
                for (i in 0 until geonamesArray.length()) {
                    val countryObject = geonamesArray.getJSONObject(i)
                    val countryName = countryObject.getString("name")
                    countryNames.add(countryName)
                    println(countryName)
                }
            } else {
                println("No geonames found in the response")
            }
        } finally {
            connection.disconnect()
        }
    }

suspend fun fetchCitiesOfCountry(country: String, cityNames: MutableList<String>, startRow: Int) =
    withContext(Dispatchers.IO) {
        val urlString =
            "http://api.geonames.org/searchJSON?country=$country&cities=cities15000&maxRows=10&username=burakgunduz22"

        val url = URL(urlString)
        val connection = url.openConnection() as HttpURLConnection
        try {
            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            val response = reader.readText()
            reader.close()

            // Parse the JSON response to get the city names
            val jsonObject = JSONObject(response)

            if (jsonObject.has("geonames")) {
                val geonamesArray = jsonObject.getJSONArray("geonames")
                cityNames.removeAt(0)
                for (i in 0 until geonamesArray.length()) {
                    val cityObject = geonamesArray.getJSONObject(i)
                    val cityName = cityObject.getString("name")
                    cityNames.add(cityName)
                    println(cityName)
                }
            } else {
                println("No geonames found in the response")
            }
        } finally {
            connection.disconnect()
        }
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExposedDropdownMenu(
    items: List<String>,
    onItemSelected: (String) -> Unit,
) {

    var expanded by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val selected = remember { mutableStateOf("") }
    val filteredCountryNames = remember { mutableStateOf(items) }
    var filteringOptions: List<String> by remember { mutableStateOf(items) }
    ExposedDropdownMenuStack(
        textField = {
            OutlinedTextField(
                value = selected.value,
                onValueChange = {
                    selected.value = it
                    expanded = true
                    filteringOptions =
                        filteredCountryNames.value.filter {
                            it.contains(
                                selected.value,
                                ignoreCase = true
                            )
                        }

                },
                trailingIcon = {
                    val rotation by animateFloatAsState(if (expanded) 180F else 0F)
                    Icon(
                        rememberVectorPainter(Icons.Default.ArrowDropDown),
                        contentDescription = "Dropdown Arrow",
                        Modifier.rotate(rotation),
                    )

                },
            )

        },
        dropdownMenu = { boxWidth, itemHeight ->
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it },
                Modifier
                    .width(boxWidth)
                    .wrapContentSize(Alignment.TopStart)
            ) {
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier
                        .height(150.dp),
                ) {
                    items.forEach { item ->
                        DropdownMenuItem(
                            modifier = Modifier
                                .height(itemHeight)
                                .width(boxWidth),
                            onClick = {
                                expanded = false
                                selected.value = item
                            }, text = { Text(item) })
                    }
                }
            }

        }

    )
}

@Composable
private fun ExposedDropdownMenuStack(
    textField: @Composable () -> Unit,
    dropdownMenu: @Composable (boxWidth: Dp, itemHeight: Dp) -> Unit
) {
    SubcomposeLayout { constraints ->
        val textFieldPlaceable =
            subcompose(ExposedDropdownMenuSlot.TextField, textField).first().measure(constraints)
        val dropdownPlaceable = subcompose(ExposedDropdownMenuSlot.Dropdown) {
            dropdownMenu(textFieldPlaceable.width.toDp(), textFieldPlaceable.height.toDp())
        }.first().measure(constraints)
        layout(textFieldPlaceable.width, textFieldPlaceable.height) {
            textFieldPlaceable.placeRelative(0, 0)
            dropdownPlaceable.placeRelative(0, textFieldPlaceable.height)
        }
    }
}

private enum class ExposedDropdownMenuSlot { TextField, Dropdown }