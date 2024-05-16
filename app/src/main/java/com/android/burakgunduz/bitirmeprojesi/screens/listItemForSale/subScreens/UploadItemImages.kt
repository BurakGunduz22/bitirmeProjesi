package com.android.burakgunduz.bitirmeprojesi.screens.listItemForSale.subScreens

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import coil.compose.SubcomposeAsyncImage
import com.android.burakgunduz.bitirmeprojesi.ui.theme.fonts.robotoFonts
import com.android.burakgunduz.bitirmeprojesi.viewModels.CountryInfo
import com.android.burakgunduz.bitirmeprojesi.viewModels.LocationViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.UUID

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UploadItemImages(
    images: MutableState<List<Uri>>, screenNumber: MutableState<Int>,
    context: Context = LocalContext.current,
    countryNames: State<List<CountryInfo>?>,
    locationViewModel: LocationViewModel,
    coroutineScope: CoroutineScope,
) {
    if (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_MEDIA_IMAGES
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        Log.d("Hop", "ListItemScreen: Permission not granted")
        ActivityCompat.requestPermissions(
            context as Activity,
            arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
            0
        )
    }
    val colorStops = arrayOf(
        0.0f to Color.hsl(0f, 0f, 0f, 0f),
        0.4f to Color.hsl(0f, 0f, 0.1f, 0.1f),
        1f to Color.Black
    )
    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) {
            images.value = it
        }
    val onePhotoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        images.value = listOf(it!!)
    }
    val onePhototaker = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {

    }

    val pagerState = rememberPagerState(pageCount = { images.value.size })
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        if (images.value.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
                    .fillMaxHeight(0.8f)
            ) {
                HorizontalPager(state = pagerState) { page ->
                    val uri = images.value[page]
                    Box(contentAlignment = Alignment.Center) {
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
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (images.value.isNotEmpty()) {
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
                    if (countryNames.value == null) {
                        locationViewModel.fetchCountryNames()
                    }
                }
            }) {
                Text("Select Image")
            }
            Button(onClick = {
                coroutineScope.launch {
                    val activityOptionsCompat: ActivityOptionsCompat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                            context as Activity
                        )
                    val imageUri = bitmapToUri(context)
                    onePhototaker.launch(imageUri,activityOptionsCompat)
                    if (countryNames.value == null) {
                        locationViewModel.fetchCountryNames()
                    }
                }
            }) {
                Text("Take a Photo")
            }
        }

    }
}

fun bitmapToUri(context: Context): Uri {
    // Get the external storage directory
    val filesDir: File = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
    // Create a file to save the bitmap
    val imageFile = File(filesDir, "image_" + UUID.randomUUID().toString() + ".png")

    var fos: FileOutputStream? = null
    try {
        fos = FileOutputStream(imageFile)
        // Use the compress method on the bitmap object to write image to the OutputStream
    } catch (e: IOException) {
        e.printStackTrace()
    } finally {
        try {
            fos?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    // Use the FileProvider to get a content URI
    return Uri.fromFile(imageFile)
}