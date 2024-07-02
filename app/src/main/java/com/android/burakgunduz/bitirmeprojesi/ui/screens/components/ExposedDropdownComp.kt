package com.android.burakgunduz.bitirmeprojesi.ui.screens.components

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import com.android.burakgunduz.bitirmeprojesi.viewModels.CityInfo
import com.android.burakgunduz.bitirmeprojesi.viewModels.CountryInfo
import com.android.burakgunduz.bitirmeprojesi.viewModels.TownInfo

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExposedDropdownMenu(
    updatedData: MutableState<String>,
    items: List<Any>,
    isLocationFilled: Boolean,
    nameOfDropBox: String,
    modifier: Modifier = Modifier,
    locationString: String = ""
) {
    var expanded by remember { mutableStateOf(false) }
    val selected = remember { mutableStateOf(locationString) }
    val filteredNames = rememberSaveable { mutableStateOf(items) }
    val filteringOptions = derivedStateOf {
        filteredNames.value.filter {filter ->
            when (filter) {
                is CountryInfo -> filter.name.startsWith(selected.value, ignoreCase = true)
                is CityInfo -> filter.name.startsWith(selected.value, ignoreCase = true)
                is TownInfo -> filter.name.startsWith(selected.value, ignoreCase = true)
                else -> return@filter true
            }

        }
    }

    val size = filteringOptions.value.size
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selected.value,
            onValueChange = {
                selected.value = it
            },
            shape = AbsoluteRoundedCornerShape(16),
            enabled = isLocationFilled,
            trailingIcon = {
                val rotation by animateFloatAsState(if (expanded) 180F else 0F, label = "")
                Icon(
                    rememberVectorPainter(Icons.Default.ArrowDropDown),
                    contentDescription = "Dropdown Arrow",
                    Modifier.rotate(rotation),
                )
            },
            label = {
                Text(text = nameOfDropBox)
            },
            modifier = Modifier.menuAnchor(),
            maxLines = 1
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
                updatedData.value = selected.value
            },
            modifier = Modifier.size(
                280.dp,
                if (size > 3) 160.dp else ((50 * size) + 10).dp
            )
        ) {
            filteringOptions.value.map { item ->
                DropdownMenuItem(
                    onClick = {
                        expanded = false
                        when (item) {
                            is CountryInfo -> {
                                selected.value = item.name
                                updatedData.value = item.name
                            }
                            is CityInfo -> {
                                selected.value = item.name
                                updatedData.value = item.name
                            }
                            is TownInfo -> {
                                selected.value = item.name
                                updatedData.value = item.name
                            }
                        }
                    },
                    text = {
                        when (item) {
                            is CountryInfo -> Text(item.name)
                            is CityInfo -> Text(item.name)
                            is TownInfo -> Text(item.name)
                            else -> Text("")
                        }
                    },
                    modifier = Modifier.size(280.dp, 50.dp)
                )
            }
        }
    }
}