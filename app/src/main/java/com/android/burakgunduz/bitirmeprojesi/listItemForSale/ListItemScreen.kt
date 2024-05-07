package com.android.burakgunduz.bitirmeprojesi.listItemForSale

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.android.burakgunduz.bitirmeprojesi.itemViewModel.Item
import com.android.burakgunduz.bitirmeprojesi.itemViewModel.ItemViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListItemScreen(viewModel: ItemViewModel) {
    val context = LocalContext.current
    val screenNumber = remember { mutableStateOf(0) }
    val images = remember { mutableStateOf(listOf<Uri>()) }
    val coroutineScope = rememberCoroutineScope()
    val itemDetails = viewModel.itemDetails.value
    Surface(modifier = Modifier.fillMaxSize()) {
        if (images.value.isNotEmpty()){
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
                Button(onClick = { viewModel.saveItem(images.value[0]) }) {
                    Text(text = "Save Item")
                }
            }
        }


        when (screenNumber.value) {
            0 -> PhotoSelection(
                images = images,
                coroutineScope = coroutineScope,
                screenNumber = screenNumber
            )

            1 -> ListItemDetails(
                images = images.value,
                itemDetails = itemDetails,
                screenNumber = screenNumber
            )
        }
    }
}

@Composable
fun ListItemDetails(
    images: List<Uri>,
    itemDetails: Item?,
    screenNumber: MutableState<Int>
) {
    Column {
        if (itemDetails != null) {
            OutlinedTextField(value = itemDetails.itemName, onValueChange = {
                itemDetails.itemName = it
            }, label = { Text("Title") })
            OutlinedTextField(value = itemDetails.itemBrand, onValueChange = {
                itemDetails.itemName = it
            }, label = { Text("Brand") })
            OutlinedTextField(value = itemDetails.itemCategory, onValueChange = {
                itemDetails.itemName = it
            }, label = { Text("ItemCategory") })
            DropdownMenu(expanded = false, onDismissRequest = { /*TODO*/ }) {
                DropdownMenuItem(
                    text = { Text("New") },
                    onClick = { itemDetails.itemCondition = 0 })
                DropdownMenuItem(
                    text = { Text("Used") },
                    onClick = { itemDetails.itemCondition = 1 })
                DropdownMenuItem(
                    text = { Text("Refurbished") },
                    onClick = { itemDetails.itemCondition = 2 })
            }
            OutlinedTextField(
                value = itemDetails.itemDesc,
                onValueChange = {
                    itemDetails.itemName = it
                },
                label = { Text("Description") }
            )
            OutlinedTextField(value = itemDetails.itemPrice.toString(), onValueChange = {
                itemDetails.itemName = it
            }, label = { Text("Price") })
        }
    }
}

@Composable
fun PhotoSelection(
    images: MutableState<List<Uri>>,
    coroutineScope: CoroutineScope,
    screenNumber: MutableState<Int>
) {
    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) {
            images.value = it
        }
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        if (images != null) {
            LazyVerticalGrid(columns = GridCells.Fixed(3)) {
                items(images.value) { index ->
                    SubcomposeAsyncImage(
                        model = index,
                        contentDescription = null,
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier.size(100.dp)
                    )
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

                }) {
                    Text("Next")
                }
            }
            Button(onClick = {
                coroutineScope.launch {
                    galleryLauncher.launch("image/*")
                }
            }) {
                Text("Select Image")
            }
        }

    }
}
