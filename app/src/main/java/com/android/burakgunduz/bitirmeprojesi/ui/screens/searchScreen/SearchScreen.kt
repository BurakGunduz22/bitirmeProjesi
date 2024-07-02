package com.android.burakgunduz.bitirmeprojesi.ui.screens.searchScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.android.burakgunduz.bitirmeprojesi.ui.screens.components.FakeTopBar
import com.android.burakgunduz.bitirmeprojesi.ui.screens.components.ItemCard
import com.android.burakgunduz.bitirmeprojesi.ui.theme.fonts.robotoFonts
import com.android.burakgunduz.bitirmeprojesi.viewModels.Categories
import com.android.burakgunduz.bitirmeprojesi.viewModels.ItemViewModel
import com.android.burakgunduz.bitirmeprojesi.viewModels.SubCategories
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun SearchScreen(
    itemViewModel: ItemViewModel,
    navController: NavController,
    isDarkModeOn: Boolean,
    userInfo: MutableState<String?>,
    auth: FirebaseAuth,
) {
    var query by remember { mutableStateOf("") }
    val isItemsLoaded = remember { mutableStateOf(false) }
    userInfo.value = userInfo.value ?: auth.currentUser?.uid
    val focusManager = LocalFocusManager.current
    val selectedCategory = remember { mutableStateOf<Categories?>(null) }
    val selectedSubCategory = remember { mutableStateOf<SubCategories?>(null) }
    // Define state for search results and loading indicator
    val items by itemViewModel.items.collectAsState()
    val categories by itemViewModel.categoriesList.observeAsState(emptyList())
    val subCategories by itemViewModel.subCategoriesList.observeAsState(emptyList())
    val itemShowcaseImage by itemViewModel.itemShowcaseImages.observeAsState()
    val coroutineScope = rememberCoroutineScope()
    // Load item categories on composition
    LaunchedEffect(Unit) {
        itemViewModel.loadItemCategories()
    }

    // Perform the search when query, selectedCategory, or selectedSubCategory changes
    LaunchedEffect(query, selectedCategory.value, selectedSubCategory.value) {
        isItemsLoaded.value = false
        itemViewModel.searchItemsIn(query, selectedCategory.value, selectedSubCategory.value) {
            isItemsLoaded.value = it
        }
    }

    // Load showcase images when items are loaded
    LaunchedEffect(isItemsLoaded.value) {
        if (isItemsLoaded.value) {
            itemViewModel.loadShowcaseImages(items) { isItemsLoaded.value = it }
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 25.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            FakeTopBar(navController = navController, screenName = "Search")

            TextField(
                value = query,
                onValueChange = { query = it },
                label = {
                    Text(
                        "Search",
                        fontFamily = robotoFonts,
                        fontWeight = FontWeight.Bold,
                    )
                },
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(10.dp)
                    .clip(AbsoluteRoundedCornerShape(25)),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus()
                }),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = "Search"
                    )
                }
            )

            // Category Chips
            // Category Chips
            LazyRow(
                modifier = Modifier.padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(categories) { _, category ->
                    FilterChip(
                        selected = selectedCategory.value == category,
                        onClick = {
                            if (selectedCategory.value == category) {
                                // Deselect the current category and reset subcategory
                                selectedCategory.value = null
                                selectedSubCategory.value = null
                                coroutineScope.launch {
                                    itemViewModel.searchItemsIn(
                                        query,
                                        null,
                                        null
                                    ) {
                                        isItemsLoaded.value = it
                                    }
                                }
                            } else {
                                selectedCategory.value = category
                                selectedSubCategory.value = null
                                itemViewModel.loadSubItemCategories(category.categoryID)
                                coroutineScope.launch {
                                    itemViewModel.searchItemsIn(
                                        query,
                                        selectedCategory.value,
                                        selectedSubCategory.value
                                    ) {
                                        isItemsLoaded.value = it
                                    }
                                }
                            }
                        },
                        label = {
                            Text(category.categoryName)
                        },
                        trailingIcon = {
                            if (selectedCategory.value == category) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear",
                                    modifier = Modifier
                                        .clip(AbsoluteRoundedCornerShape(4.dp))
                                        .clickable {
                                            // Clear selection
                                            selectedCategory.value = null
                                            selectedSubCategory.value = null
                                            coroutineScope.launch {
                                                itemViewModel.searchItemsIn(
                                                    query,
                                                    null,
                                                    null
                                                ) {
                                                    isItemsLoaded.value = it
                                                }
                                            }
                                        }
                                        .padding(4.dp)
                                )
                            }
                        }
                    )
                }
            }

            LazyRow(
                modifier = Modifier.padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(subCategories) { _, subCategory ->
                    FilterChip(
                        selected = selectedSubCategory.value == subCategory,
                        onClick = {
                            if (selectedSubCategory.value == subCategory) {
                                // Deselect the current subcategory
                                selectedSubCategory.value = null
                                coroutineScope.launch {
                                    itemViewModel.searchItemsIn(
                                        query,
                                        selectedCategory.value,
                                        null
                                    ) {
                                        isItemsLoaded.value = it
                                    }
                                }
                            } else {
                                selectedSubCategory.value = subCategory
                                coroutineScope.launch {
                                    itemViewModel.searchItemsIn(
                                        query,
                                        selectedCategory.value,
                                        selectedSubCategory.value
                                    ) {
                                        isItemsLoaded.value = it
                                    }
                                }
                            }
                        },
                        label = {
                            Text(subCategory.subCategoryName)
                        },
                        trailingIcon = {
                            if (selectedSubCategory.value == subCategory) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear",
                                    modifier = Modifier
                                        .clickable {
                                            // Clear selection
                                            selectedSubCategory.value = null
                                            coroutineScope.launch {
                                                itemViewModel.searchItemsIn(
                                                    query,
                                                    selectedCategory.value,
                                                    null
                                                ) {
                                                    isItemsLoaded.value = it
                                                }
                                            }
                                        }
                                        .padding(4.dp)
                                )
                            }
                        }
                    )
                }
            }


            // Display items or loading indicator
            if (isItemsLoaded.value) {
                val itemImages = itemShowcaseImage?.map { it }
                LazyColumn(horizontalAlignment = Alignment.CenterHorizontally) {
                    itemsIndexed(items) { _, document ->
                        val toggleButtonChecked =
                            remember { mutableStateOf(itemViewModel.isItemLiked(document.itemID)) }
                        val itemImagesEqualled =
                            itemImages?.find { it.itemID == document.itemID }?.uri
                        ItemCard(
                            document,
                            itemImagesEqualled.toString(),
                            isDarkModeOn,
                            navController,
                            toggleButtonChecked,
                            {
                                itemViewModel.addLikedItem(userInfo.value!!, document.itemID)
                                toggleButtonChecked.value = true
                            },
                            {
                                itemViewModel.removeLikedItems(userInfo.value!!, document.itemID)
                                toggleButtonChecked.value = false
                            },
                            userInfo.value == document.userID,
                        )
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(modifier = Modifier.size(100.dp))
                }
            }
        }
    }
}


