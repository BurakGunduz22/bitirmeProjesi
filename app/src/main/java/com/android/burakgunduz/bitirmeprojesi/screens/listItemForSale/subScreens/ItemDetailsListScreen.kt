package com.android.burakgunduz.bitirmeprojesi.screens.listItemForSale.subScreens

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBackIos
import androidx.compose.material.icons.automirrored.outlined.ArrowForwardIos
import androidx.compose.material.icons.automirrored.outlined.Wysiwyg
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import com.android.burakgunduz.bitirmeprojesi.screens.listItemForSale.components.CategoryDropDownMenu
import com.android.burakgunduz.bitirmeprojesi.screens.listItemForSale.components.ItemDetailsTextfield
import com.android.burakgunduz.bitirmeprojesi.screens.listItemForSale.components.ProgressBar
import com.android.burakgunduz.bitirmeprojesi.ui.theme.fonts.archivoFonts
import com.android.burakgunduz.bitirmeprojesi.viewModels.ItemViewModel
import com.android.burakgunduz.bitirmeprojesi.viewModels.SubCategories
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListItemDetails(
    itemDetails: MutableList<String>,
    screenNumber: MutableState<Int>,
    coroutineScope: CoroutineScope,
    nextScreen: MutableState<Boolean>,
    itemViewModel: ItemViewModel,
) {

    val dropControl = remember { mutableStateOf(false) }
    val title = remember { mutableStateOf(itemDetails.getOrNull(0) ?: "") }
    val brand = remember { mutableStateOf(itemDetails.getOrNull(1) ?: "") }
    val itemCategory = remember { mutableStateOf(itemDetails.getOrNull(2) ?: "") }
    val itemSubCategory = remember { mutableStateOf(itemDetails.getOrNull(3) ?: "") }
    val description = remember { mutableStateOf(itemDetails.getOrNull(4) ?: "") }
    val price = remember { mutableStateOf(itemDetails.getOrNull(5) ?: "") }
    val condition = remember { mutableStateOf(itemDetails.getOrNull(6) ?: "") }
    val itemCategories = itemViewModel.categoriesList.observeAsState()
    val itemSubCategories = itemViewModel.subCategoriesList.observeAsState()
    if (itemCategories.value.isNullOrEmpty()) {
        itemViewModel.loadItemCategories()
    }
    val subCategoriesList = remember { mutableListOf<SubCategories>() }
    val loadItemNames = remember {
        mutableStateOf(false)
    }
    val categoryList = itemCategories.value?.map { it } ?: listOf()
    val focusManager = LocalFocusManager.current
    val categoryLoaded = remember {
        mutableStateOf(false)
    }
    val itemCategoryName = remember {
        mutableStateOf("")
    }
    val itemSubCategoryName = remember {
        mutableStateOf("")
    }
    LaunchedEffect(itemCategory.value) {
        if (itemCategory.value.isNotEmpty()) {
            val category = categoryList.find { it.categoryID == itemCategory.value }
            itemCategoryName.value = category?.categoryName ?: ""
            // Load subcategories when category changes
            coroutineScope.launch {
                itemViewModel.loadSubItemCategories(itemCategory.value)
                categoryLoaded.value = true
            }
        }
    }

    LaunchedEffect(itemSubCategories.value) {
        subCategoriesList.clear() // Clear subcategories
        subCategoriesList.addAll(itemSubCategories.value ?: listOf())

        if (!itemSubCategory.value.isNullOrEmpty()) {
            val subCategory = subCategoriesList.find { it.subCategoryID == itemSubCategory.value }
            itemSubCategoryName.value = subCategory?.subCategoryName ?: ""
        }
    }


    Log.e("LogDetails", itemDetails.toString())
    Log.e("isNextScreen", nextScreen.toString())
    LaunchedEffect(nextScreen) {
        delay(500)
        nextScreen.value = true
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedVisibility(
            visible = nextScreen.value,
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Add Item Details",
                    style = MaterialTheme.typography.headlineLarge,
                    fontFamily = archivoFonts,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-1).sp,
                    fontSize = 30.sp
                )
                Column(
                    modifier = Modifier.fillMaxHeight(0.8f),
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    BackHandler {
                        coroutineScope.launch {
                            screenNumber.value = 0
                            itemDetails.clear()
                            itemDetails.addAll(
                                listOf(
                                    title.value,
                                    brand.value,
                                    itemCategory.value,
                                    itemSubCategory.value,
                                    description.value,
                                    price.value,
                                    condition.value
                                )
                            )
                        }
                    }
                    ItemDetailsTextfield(
                        fieldName = "Title",
                        fieldValue = title.value,
                        onValueChange = { title.value = it },
                        focusManager = focusManager,
                        maxLength = 50
                    )
                    ItemDetailsTextfield(
                        fieldName = "Brand",
                        fieldValue = brand.value,
                        onValueChange = { brand.value = it },
                        focusManager = focusManager
                    )
                    CategoryDropDownMenu(
                        updatedData = itemCategory,
                        items = categoryList,
                        isCategorySelected = !categoryList.isNullOrEmpty(),
                        nameOfDropBox = "Category",
                        categoryName = itemCategoryName.value
                    )
                    // Load subcategories when category changes
                    LaunchedEffect(itemCategory.value) {
                        if (itemCategory.value.isNotEmpty()) {
                            coroutineScope.launch {
                                itemViewModel.loadSubItemCategories(itemCategory.value)
                                categoryLoaded.value = true
                            }
                        }
                    }
                    LaunchedEffect(itemSubCategories.value) {
                        subCategoriesList.clear() // Clear subcategories
                        subCategoriesList.addAll(itemSubCategories.value ?: listOf())
                    }
                    CategoryDropDownMenu(
                        updatedData = itemSubCategory,
                        items = subCategoriesList,
                        isCategorySelected = categoryLoaded.value,
                        nameOfDropBox = "Sub-Category",
                        categoryName = itemSubCategoryName.value
                    )
                    ItemDetailsTextfield(
                        fieldName = "Price",
                        fieldValue = price.value,
                        onValueChange = { price.value = it },
                        focusManager = focusManager,
                        keyboardOptions = KeyboardType.Number,
                        suffixText = "€"
                    )
                    ItemDetailsTextfield(
                        fieldName = "Description",
                        fieldValue = description.value,
                        isItSingleLine = false,
                        onValueChange = { description.value = it },
                        minLineCount = 6,
                        maxLineCount = 6,
                        focusManager = focusManager,
                        cornerRound = 8,
                        maxLength = 200
                    )
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
                                Text("Condition")
                            },
                            shape = AbsoluteRoundedCornerShape(16),
                            readOnly = true,
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(0.8f),
                            trailingIcon = {
                                Icon(
                                    imageVector = if (dropControl.value) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                    contentDescription = "Dropdown Icon",
                                )
                            }
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
                }
            }
        }
        val isMet =
            title.value.isNotEmpty() && brand.value.isNotEmpty() && itemCategory.value.isNotEmpty() && itemSubCategory.value.isNotEmpty() && description.value.isNotEmpty() && price.value.isNotEmpty() && condition.value.isNotEmpty()
        ProgressBar(
            isProgressRequirementsMet = isMet,
            currentIcon = Icons.AutoMirrored.Outlined.Wysiwyg,
            nextIcon = Icons.AutoMirrored.Outlined.ArrowForwardIos,
            previousIcon = Icons.AutoMirrored.Outlined.ArrowBackIos,
            nextScreen = {
                screenNumber.value = 2
                itemDetails.clear()
                itemDetails.addAll(
                    listOf(
                        title.value,
                        brand.value,
                        itemCategory.value,
                        itemSubCategory.value,
                        description.value,
                        price.value,
                        condition.value
                    )
                )
                nextScreen.value = false
            },
            previousScreen = {
                screenNumber.value = 0
                itemDetails.clear()
                itemDetails.addAll(
                    listOf(
                        title.value,
                        brand.value,
                        itemCategory.value,
                        itemSubCategory.value,
                        description.value,
                        price.value,
                        condition.value
                    )
                )
                nextScreen.value = false
            }
        )
    }
}
