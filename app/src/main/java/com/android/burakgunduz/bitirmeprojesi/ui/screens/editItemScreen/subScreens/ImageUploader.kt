package com.android.burakgunduz.bitirmeprojesi.ui.screens.listItemForSale.subScreens

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.android.burakgunduz.bitirmeprojesi.ui.screens.components.PhotoSelectorModalSheet
import com.android.burakgunduz.bitirmeprojesi.ui.screens.components.photoTaken
import com.android.burakgunduz.bitirmeprojesi.ui.theme.fonts.robotoFonts
import com.android.burakgunduz.bitirmeprojesi.viewModels.CountryInfo
import com.android.burakgunduz.bitirmeprojesi.viewModels.ItemViewModel
import com.android.burakgunduz.bitirmeprojesi.viewModels.LocationViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ImageUploader(
    imageUri: MutableList<Uri>,
    itemViewModel: ItemViewModel,
    context: Context = LocalContext.current,
    coroutineScope: CoroutineScope,
    locationViewModel: LocationViewModel,
    itemID: String,
    countryNames: State<List<CountryInfo>?>,
) {
    val colorStops = arrayOf(
        0.0f to Color.hsl(0f, 0f, 0f, 0f),
        0.4f to Color.hsl(0f, 0f, 0.1f, 0.1f),
        1f to Color.Black
    )

    val updatedImageUri by rememberUpdatedState(imageUri)
    val imageList = remember { mutableStateListOf(*updatedImageUri.toTypedArray()) }
    val tempPhoto = remember { mutableStateOf(Uri.EMPTY) }
    val photoTakenSuccess = remember { mutableStateOf(false) }
    val pageIndex = remember { mutableIntStateOf(0) }
    val sheetState = rememberModalBottomSheetState()
    val showBottomSheet = remember { mutableStateOf(false) }
    val pagerState = rememberPagerState(pageCount = { 6 })
    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
            if (it != null) {
                tempPhoto.value = it
                Log.e("PhotoTaken2", tempPhoto.value.toString())
                Log.e("ImageList", imageList.toString())
                Log.e("pageIndex", pageIndex.intValue.toString())
            } else {
                tempPhoto.value = Uri.EMPTY
                Log.e("PhotoTaken2", tempPhoto.value.toString())
                Log.e("pageIndex", pageIndex.intValue.toString())
                Log.e("ImageList", imageList.toString())
            }
        }
    val onePhotoTaker =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                photoTakenSuccess.value = true
                Log.e("PhotoTaken2", photoTakenSuccess.value.toString())
                imageList[pageIndex.intValue] = tempPhoto.value
                Log.e("PhotoTaken2", imageList.toString())
            } else {
                photoTakenSuccess.value = false
                Log.e("PhotoTaken2", photoTakenSuccess.value.toString())
                imageList[pageIndex.intValue] = Uri.EMPTY
                Log.e("PhotoTaken2", imageList.toString())
            }
        }

    BackHandler(showBottomSheet.value) {
        coroutineScope.launch { sheetState.hide() }
    }

    // Force recomposition when imageUri changes
    LaunchedEffect(imageUri) {
        imageList.clear()
        imageList.addAll(imageUri)
    }

    LaunchedEffect(tempPhoto.value) {
        if (tempPhoto.value != Uri.EMPTY && pageIndex.intValue != -1) {
            imageList[pageIndex.intValue] = tempPhoto.value
            Log.e("PhotoTaken2", imageList.toString())
        }
    }

    LaunchedEffect(photoTakenSuccess.value) {
        if (photoTakenSuccess.value) {
            imageList[pageIndex.intValue] = tempPhoto.value
            photoTakenSuccess.value = false
        }
    }
    LaunchedEffect(photoTakenSuccess.value) {
        Log.e("PhotoTaken", photoTakenSuccess.value.toString())
        if (photoTakenSuccess.value) {
            photoTaken(
                imageList,
                context,
                pageIndex,
                photoTakenSuccess,
                onePhotoTaker,
                tempPhoto,
                countryNames,
                locationViewModel,
                coroutineScope
            )
        }
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
                .fillMaxHeight(0.9f),
            contentAlignment = Alignment.CenterStart
        ) {
            HorizontalPager(
                state = pagerState,
                contentPadding = PaddingValues(start = 32.dp, end = 16.dp)
            ) { page ->
                val uri = imageList[page]
                OutlinedCard(
                    onClick = { showBottomSheet.value = true },
                    shape = AbsoluteRoundedCornerShape(8),
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth(0.95f)
                            .fillMaxHeight(0.9f)
                    ) {
                        if (uri == Uri.EMPTY) {
                            Icon(
                                Icons.Outlined.PhotoLibrary,
                                contentDescription = "Photos",
                                modifier = Modifier.size(50.dp)
                            )
                        } else {
                            SubcomposeAsyncImage(
                                model = uri,
                                contentDescription = null,
                                contentScale = ContentScale.FillWidth,
                                modifier = Modifier.fillMaxSize()
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Brush.verticalGradient(colorStops = colorStops)),
                                contentAlignment = Alignment.BottomStart
                            ) {
                                Text(
                                    text = if (page == 0) "Showcase Photo" else "Photo ${page + 1}",
                                    color = Color.White,
                                    fontFamily = robotoFonts,
                                    style = MaterialTheme.typography.displayMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 80.dp, start = 10.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        FloatingActionButton(onClick = {
            Log.e("ImageList", imageList.toString())
            itemViewModel.replaceItemImages(
                itemID,
                imageList
            )

        }) {
            Text("Add Photo")
        }
    }

    PhotoSelectorModalSheet(
        showBottomSheet = showBottomSheet,
        pageIndex = pageIndex,
        images = imageList,
        tempPhoto = tempPhoto,
        context = context,
        locationViewModel = locationViewModel,
        coroutineScope = coroutineScope,
        page = pagerState.currentPage,
        galleryLauncher = galleryLauncher,
        countryNames = countryNames,
        sheetState = sheetState,
        photoTaker = onePhotoTaker,
        photoTakenSuccess = photoTakenSuccess
    )
}

