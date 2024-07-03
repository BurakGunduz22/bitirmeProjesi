package com.android.burakgunduz.bitirmeprojesi.ui.screens.editItemScreen.subScreens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.android.burakgunduz.bitirmeprojesi.ui.screens.components.CategoryDropDownMenu
import com.android.burakgunduz.bitirmeprojesi.viewModels.Item
import com.android.burakgunduz.bitirmeprojesi.viewModels.ItemViewModel
import com.android.burakgunduz.bitirmeprojesi.viewModels.SubCategories

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditItemDetails(
    itemDetails: Item,
    itemViewModel: ItemViewModel,
    navController: NavController
) {
    val dropControl = remember { mutableStateOf(false) }
    val categoryDropControl = remember { mutableStateOf(false) }
    val subCategoryDropControl = remember { mutableStateOf(false) }
    val title = remember { mutableStateOf(itemDetails.itemName) }
    val brand = remember { mutableStateOf(itemDetails.itemBrand) }
    val itemCategory = remember { mutableStateOf(itemDetails.itemCategory) }
    val itemSubCategory = remember { mutableStateOf(itemDetails.itemSubCategory) }
    val description = remember { mutableStateOf(itemDetails.itemDesc) }
    val price = remember { mutableStateOf(itemDetails.itemPrice.toString()) }
    val condition = remember { mutableStateOf(itemDetails.itemCondition.toString()) }
    val itemCategories = itemViewModel.categoriesList.observeAsState()
    val itemSubCategories = itemViewModel.subCategoriesList.observeAsState()
    val subCategoriesList = remember { mutableListOf<SubCategories>() }
    val focusManager = LocalFocusManager.current
    val itemCategoryName = remember {
        mutableStateOf(
            itemCategories.value?.find { it.categoryID == itemDetails.itemCategory }?.categoryName
                ?: ""
        )
    }

    LaunchedEffect(Unit) {
        itemViewModel.loadItemCategories()
    }

    LaunchedEffect(itemCategory.value) {
        if (itemCategory.value.isNotEmpty()) {
            itemViewModel.loadSubItemCategories(itemCategory.value)
        }
    }
    val categoryList = itemCategories.value?.map { it } ?: listOf()

    LaunchedEffect(itemSubCategories.value) {
        subCategoriesList.clear()
        subCategoriesList.addAll(itemSubCategories.value ?: listOf())
    }

    LaunchedEffect(itemSubCategory.value) {
        if (itemSubCategory.value.isNotEmpty()) {
            val subCategory = subCategoriesList.find { it.subCategoryID == itemSubCategory.value }
            subCategoryDropControl.value = true
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Edit Item Details",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth(0.8f)

        )
        OutlinedTextField(
            value = title.value,
            onValueChange = { title.value = it },
            label = { Text("Title") },
            modifier = Modifier
                .fillMaxWidth(0.8f),
            shape = AbsoluteRoundedCornerShape(16),
        )
        OutlinedTextField(
            value = brand.value,
            onValueChange = { brand.value = it },
            label = { Text("Brand") },
            modifier = Modifier
                .fillMaxWidth(0.8f),
            shape = AbsoluteRoundedCornerShape(16),
        )
        // Category dropdown menu
        if (categoryList.isNotEmpty()) {
            CategoryDropDownMenu(
                updatedData = itemCategory,
                items = categoryList,
                isCategorySelected = true,
                nameOfDropBox = "Category",
                categoryName = itemCategory.value,
            )
        } else {
            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
        }

            CategoryDropDownMenu(
                updatedData = itemSubCategory,
                items = subCategoriesList,
                isCategorySelected = true,
                nameOfDropBox = "Sub-Category",
                categoryName = itemSubCategory.value,
            )


        OutlinedTextField(
            value = description.value,
            onValueChange = { description.value = it },
            label = { Text("Description") },
            minLines = 6,
            modifier = Modifier.fillMaxWidth(0.8f),
            shape = AbsoluteRoundedCornerShape(16),
        )
        OutlinedTextField(
            value = price.value,
            onValueChange = {
                if (it.all { char -> char.isDigit() }) {
                    price.value = it
                }
            },
            label = { Text("Price") },
            modifier = Modifier
                .fillMaxWidth(0.8f),
            shape = AbsoluteRoundedCornerShape(16),
            suffix = { Text("€") }
        )
        ExposedDropdownMenuBox(
            expanded = dropControl.value,
            onExpandedChange = { dropControl.value = it },
            modifier = Modifier
                .fillMaxWidth(0.8f)
        ) {
            OutlinedTextField(
                value = when (condition.value) {
                    0.toString() -> "New"
                    1.toString() -> "Used"
                    2.toString() -> "Refurbished"
                    else -> "Select Condition"
                },
                onValueChange = {},
                label = { Text("Condition") },
                readOnly = true,
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(0.8f),
                trailingIcon = {
                    val rotation by animateFloatAsState(
                        if (dropControl.value) 180F else 0F,
                        label = ""
                    )
                    Icon(
                        rememberVectorPainter(Icons.Default.ArrowDropDown),
                        contentDescription = "Dropdown Arrow",
                        Modifier.rotate(rotation),
                    )
                },
                shape = AbsoluteRoundedCornerShape(16),
            )
            ExposedDropdownMenu(
                expanded = dropControl.value,
                onDismissRequest = { dropControl.value = false },
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                DropdownMenuItem(
                    text = { Text("New") },
                    onClick = {
                        condition.value = 0.toString()
                        dropControl.value = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Used") },
                    onClick = {
                        condition.value = 1.toString()
                        dropControl.value = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Refurbished") },
                    onClick = {
                        condition.value = 2.toString()
                        dropControl.value = false
                    }
                )
            }
        }
        Button(onClick = {
            val itemDetailsList = mutableListOf(
                title.value,
                brand.value,
                itemCategory.value,
                itemSubCategory.value,
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

