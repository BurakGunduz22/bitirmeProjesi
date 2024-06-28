package com.android.burakgunduz.bitirmeprojesi.ui.screens.editItemScreen.subScreens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.android.burakgunduz.bitirmeprojesi.viewModels.Item
import com.android.burakgunduz.bitirmeprojesi.viewModels.ItemViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditItemDetails(
    itemDetails: Item,
    itemViewModel: ItemViewModel,
    navController: NavController
) {
    val dropControl = remember { mutableStateOf(false) }
    val title = remember { mutableStateOf(itemDetails.itemName) }
    val brand = remember { mutableStateOf(itemDetails.itemBrand) }
    val itemCategory = remember { mutableStateOf(itemDetails.itemCategory) }
    val description = remember { mutableStateOf(itemDetails.itemDesc) }
    val price = remember { mutableStateOf(itemDetails.itemPrice.toString()) }
    val condition = remember { mutableStateOf(itemDetails.itemCondition.toString()) }
    Log.e("LogDetails", itemDetails.toString())
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
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
            label = { Text("Description") },
            minLines = 6,
        )
        OutlinedTextField(value = price.value, onValueChange = {
            price.value = it
        }, label = { Text("Price") })
        ExposedDropdownMenuBox(
            expanded = dropControl.value,
            onExpandedChange = { dropControl.value = it }) {
            OutlinedTextField(
                value = when (condition.value) {
                    0.toString() -> "New"
                    1.toString() -> "Used"
                    2.toString() -> "Refurbished"
                    else -> "Select Condition"
                },
                onValueChange = {},
                label = {
                    Text(
                        text = when (condition.value) {
                            0.toString() -> "New"
                            1.toString() -> "Used"
                            2.toString() -> "Refurbished"
                            else -> "Select Condition"
                        }
                    )
                },
                readOnly = true,
                modifier = Modifier.menuAnchor()
            )
            ExposedDropdownMenu(
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
            val itemDetailsList = mutableListOf(
                title.value,
                brand.value,
                itemCategory.value,
                description.value,
                price.value,
                condition.value
            )
            itemViewModel.updateItemDetails(itemDetailsList, itemDetails.itemID)
            navController.popBackStack()
        }) {
            Text("Save")
        }
    }
}