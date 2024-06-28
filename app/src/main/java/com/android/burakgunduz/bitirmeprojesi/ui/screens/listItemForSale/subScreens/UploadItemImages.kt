package com.android.burakgunduz.bitirmeprojesi.ui.screens.listItemForSale.subScreens

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.automirrored.outlined.ArrowBackIos
import androidx.compose.material.icons.automirrored.outlined.ArrowForwardIos
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import com.android.burakgunduz.bitirmeprojesi.ui.screens.components.ConfirmExitDialog
import com.android.burakgunduz.bitirmeprojesi.ui.screens.components.PhotoSelectorModalSheet
import com.android.burakgunduz.bitirmeprojesi.ui.screens.components.ProgressBar
import com.android.burakgunduz.bitirmeprojesi.ui.screens.components.bitmapToUri
import com.android.burakgunduz.bitirmeprojesi.ui.screens.components.photoTaken
import com.android.burakgunduz.bitirmeprojesi.ui.theme.fonts.archivoFonts
import com.android.burakgunduz.bitirmeprojesi.viewModels.CountryInfo
import com.android.burakgunduz.bitirmeprojesi.viewModels.LocationViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun UploadItemImages(
    images: MutableList<Uri>,
    screenNumber: MutableState<Int>,
    context: Context = LocalContext.current,
    countryNames: State<List<CountryInfo>?>,
    locationViewModel: LocationViewModel,
    coroutineScope: CoroutineScope,
    navigatorController: NavController,
    nextScreen: MutableState<Boolean>,
) {
    val photoTakenSuccess = remember { mutableStateOf(false) }
    val showDialog = remember { mutableStateOf(false) }
    val tempPhoto = remember { mutableStateOf(Uri.EMPTY) }
    val pageIndex = remember { mutableIntStateOf(0) }
    val sheetState = rememberModalBottomSheetState()
    val isPhotoAdded = images[0] != Uri.EMPTY
    val colorStops = arrayOf(
        0.0f to Color.hsl(0f, 0f, 0f, 0f),
        0.4f to Color.hsl(0f, 0f, 0.1f, 0.1f),
        1f to Color.Black
    )
    Log.e("isNextScreen", nextScreen.value.toString())
    val imageUri =
        bitmapToUri(
            context
        )
    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
            if (it != null) {
                tempPhoto.value = it
            }
        }
    val onePhotoTaker =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                photoTakenSuccess.value = true
                Log.e("PhotoTaken2", photoTakenSuccess.value.toString())
                images[pageIndex.intValue] = tempPhoto.value
            } else {
                photoTakenSuccess.value = false
                Log.e("PhotoTaken2", photoTakenSuccess.value.toString())
            }
        }
    LaunchedEffect(nextScreen) {
        delay(500)
        nextScreen.value = true
    }
    LaunchedEffect(tempPhoto.value) {
        if (tempPhoto.value != Uri.EMPTY) {
            if (pageIndex.intValue != -1) {
                images[pageIndex.intValue] = tempPhoto.value
            }
        }
    }
    LaunchedEffect(photoTakenSuccess.value) {
        Log.e("PhotoTaken", photoTakenSuccess.value.toString())
        if (photoTakenSuccess.value) {
            photoTaken(
                images,
                context,
                pageIndex,
                photoTakenSuccess,
                onePhotoTaker,
                tempPhoto,
            )
        }
    }
    val showBottomSheet = remember { mutableStateOf(false) }
    val isShowcasePhotoUploaded = images[0] != Uri.EMPTY
    BackHandler(showBottomSheet.value) {
        coroutineScope.launch { sheetState.hide() }
    }
    BackHandler {
        showDialog.value = true
    }
    ConfirmExitDialog(
        showDialog = showDialog,
        onConfirm = {
            coroutineScope.launch { navigatorController.popBackStack() }
        },
        onDismiss = {
            showDialog.value = false
        }
    )
    val pagerState = rememberPagerState(pageCount = { 6 })
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AnimatedVisibility(visible = nextScreen.value) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Upload Item Images",
                    style = MaterialTheme.typography.headlineLarge,
                    fontFamily = archivoFonts,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-1).sp,
                    fontSize = 30.sp,
                )
                Column(
                    modifier = Modifier.fillMaxHeight(0.8f),
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
                            verticalAlignment = Alignment.CenterVertically,
                            contentPadding = PaddingValues(start = 32.dp, end = 16.dp),
                            userScrollEnabled = isShowcasePhotoUploaded,
                        ) { page ->
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
                                    if (images[page] == Uri.EMPTY) {
                                        // Display an "Upload Photo" button when there are no images
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                Icons.Outlined.PhotoLibrary,
                                                contentDescription = "Photos",
                                                modifier = Modifier.size(50.dp)
                                            )
                                        }
                                    } else {
                                        // Display the image when there are images
                                        val uri = images[page]
                                        SubcomposeAsyncImage(
                                            model = uri,
                                            contentDescription = null,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Brush.verticalGradient(colorStops = colorStops)),
                                        contentAlignment = Alignment.BottomStart
                                    ) {
                                        Text(
                                            text = if (page == 0) "Showcase Photo" else "Photo ${page + 1}",
                                            color = Color.White,
                                            fontFamily = archivoFonts,
                                            style = MaterialTheme.typography.headlineLarge,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 28.sp,
                                            modifier = Modifier.padding(
                                                bottom = 80.dp,
                                                start = 10.dp
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        ProgressBar(
            isProgressRequirementsMet = isPhotoAdded,
            nextScreen = {
                screenNumber.value = 1
                nextScreen.value = false
            },
            previousScreen = {
                showDialog.value = true
                nextScreen.value = false
            },
            currentIcon = Icons.Outlined.CameraAlt,
            nextIcon = Icons.AutoMirrored.Outlined.ArrowForwardIos,
            previousIcon = Icons.AutoMirrored.Outlined.ArrowBackIos,
            isThisNotFirstScreen = false
        )


        PhotoSelectorModalSheet(
            showBottomSheet = showBottomSheet,
            pageIndex = pageIndex,
            images = images,
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
}
