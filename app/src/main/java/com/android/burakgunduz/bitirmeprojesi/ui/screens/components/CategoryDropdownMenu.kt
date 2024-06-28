package com.android.burakgunduz.bitirmeprojesi.ui.screens.components

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import com.android.burakgunduz.bitirmeprojesi.viewModels.Categories
import com.android.burakgunduz.bitirmeprojesi.viewModels.SubCategories

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDropDownMenu(
    updatedData: MutableState<String>,
    items: List<Any>,
    isCategorySelected: Boolean,
    nameOfDropBox: String,
    categoryName: String
) {
    var expanded by remember { mutableStateOf(false) }
    val selected = remember { mutableStateOf(categoryName) }
    val filteredNames = rememberSaveable { mutableStateOf(items) }

    val size = filteredNames.value.size

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        OutlinedTextField(
            value = selected.value,
            onValueChange = {
                selected.value = it
            },
            shape = AbsoluteRoundedCornerShape(16),
            enabled = isCategorySelected,
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
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(0.8f),
            readOnly = true
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
                updatedData.value = selected.value
            },
            modifier = Modifier.height(
                if (size > 3) 160.dp else ((50 * size) + 10).dp
            )

        ) {
            filteredNames.value.map { item ->
                DropdownMenuItem(
                    onClick = {
                        expanded = false
                        when (item) {
                            is Categories -> {
                                selected.value = item.categoryName
                                updatedData.value = item.categoryID
                            }

                            is SubCategories -> {
                                selected.value = item.subCategoryName
                                updatedData.value = item.subCategoryID
                            }
                        }
                    },
                    text = {
                        when (item) {
                            is Categories -> Text(item.categoryName)
                            is SubCategories -> Text(item.subCategoryName)
                            else -> Text("")
                        }
                    },
                    modifier = Modifier.height(50.dp)
                )
            }

        }
    }

}