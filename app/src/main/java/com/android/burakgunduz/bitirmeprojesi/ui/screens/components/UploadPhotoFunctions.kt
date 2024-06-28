package com.android.burakgunduz.bitirmeprojesi.ui.screens.components

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.FileProvider
import com.android.burakgunduz.bitirmeprojesi.viewModels.CountryInfo
import com.android.burakgunduz.bitirmeprojesi.viewModels.LocationViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.Date
import java.util.UUID

fun addImage(
    pageIndex: MutableIntState,
    images: MutableList<Uri>,
    tempPhoto: MutableState<Uri>,
    context: Context,
    locationViewModel: LocationViewModel,
    coroutineScope: CoroutineScope,
    page: Int,
    galleryLauncher: ManagedActivityResultLauncher<String, Uri?>,
    countryNames: State<List<CountryInfo>?>
) {
    coroutineScope.launch {
        val activityOptionsCompat: ActivityOptionsCompat =
            ActivityOptionsCompat.makeSceneTransitionAnimation(
                context as Activity
            )
        galleryLauncher.launch("image/*", activityOptionsCompat)
        pageIndex.intValue = page
        images[page] = tempPhoto.value
        if (countryNames.value == null) {
            locationViewModel.fetchCountryNames()
        }
    }
}

fun captureAPhoto(
    context: Context,
    onePhotoTaker: ManagedActivityResultLauncher<Uri, Boolean>,
    images: MutableList<Uri>,
    pageIndex: MutableIntState,
    photoTakenSuccess: MutableState<Boolean>,
) {
    val imageUri = bitmapToUri(context)
    Log.e("PhotoNumber", pageIndex.toString())
    onePhotoTaker.launch(imageUri)
    if (pageIndex.intValue != -1) {
        images[pageIndex.intValue] = imageUri
    }
}
fun photoTaken(
    images: MutableList<Uri>,
    context: Context,
    pageIndex: MutableIntState,
    photoTakenSuccess: MutableState<Boolean>,
    onePhotoTaker: ManagedActivityResultLauncher<Uri, Boolean>,
    imageUri: MutableState<Uri>
) {
    Log.e("ImageUri", imageUri.toString())
    val takePictureIntent =
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(context.packageManager)?.also {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri.value)
            }
        }
    onePhotoTaker.contract.parseResult(Activity.RESULT_OK, takePictureIntent)
    images[pageIndex.intValue] = imageUri.value
    photoTakenSuccess.value = false
    Log.e("TakePictureIntent", images.toString())
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
    return FileProvider.getUriForFile(context, context.packageName + ".provider", imageFile)
}

fun getImageUri(context: Context): Uri {
    val file = File(context.filesDir, Date().time.toString() + "temp_image.jpg")
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        file
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoSelectorModalSheet(
    showBottomSheet: MutableState<Boolean>,
    pageIndex: MutableIntState,
    images: MutableList<Uri>,
    tempPhoto: MutableState<Uri>,
    context: Context,
    locationViewModel: LocationViewModel,
    coroutineScope: CoroutineScope,
    page: Int,
    galleryLauncher: ManagedActivityResultLauncher<String, Uri?>,
    photoTaker: ManagedActivityResultLauncher<Uri, Boolean>,
    countryNames: State<List<CountryInfo>?>,
    sheetState: SheetState,
    photoTakenSuccess: MutableState<Boolean>
) {
    val isPhotoInPage = images[page] != Uri.EMPTY
    if (showBottomSheet.value) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet.value = false },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                TextButton(
                    onClick = {
                        showBottomSheet.value = false
                        addImage(
                            pageIndex,
                            images,
                            tempPhoto,
                            context,
                            locationViewModel,
                            coroutineScope,
                            page,
                            galleryLauncher,
                            countryNames
                        )
                    },
                    shape = AbsoluteRoundedCornerShape(8.dp),
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .size(35.dp),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.PhotoLibrary, contentDescription = "Photos")
                        Text(
                            text = "Select Photo From Gallery",
                            modifier = Modifier.padding(start = 8.dp),
                            fontSize = 24.sp
                        )
                    }
                }
                HorizontalDivider()
                TextButton(
                    onClick = {
                        showBottomSheet.value = false
                        pageIndex.intValue = page
                        tempPhoto.value = getImageUri(context)
                        photoTaker.launch(tempPhoto.value)
                    },
                    shape = AbsoluteRoundedCornerShape(8.dp),
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .size(35.dp),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.PhotoCamera, contentDescription = "PhotosCamera")
                        Text(
                            text = "Take a Photo",
                            modifier = Modifier.padding(start = 8.dp),
                            fontSize = 24.sp
                        )
                    }
                }
                HorizontalDivider()
                TextButton(
                    onClick = {
                        showBottomSheet.value = false
                        images[page] = Uri.EMPTY
                    },
                    shape = AbsoluteRoundedCornerShape(8.dp),
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    enabled = isPhotoInPage
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .size(35.dp),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.DeleteOutline, contentDescription = "Delete")
                        Text(
                            text = "Delete Photo",
                            modifier = Modifier.padding(start = 8.dp),
                            fontSize = 24.sp
                        )
                    }
                }
            }
        }
    }
}
@Composable
fun ConfirmExitDialog(
    showDialog: MutableState<Boolean>,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (showDialog.value) {
        AlertDialog(
            shape = AbsoluteRoundedCornerShape(8),
            onDismissRequest = {
                onDismiss()
            },
            title = {
                Text(text = "Confirm Exit")
            },
            text = {
                Text("Are you sure you want to exit this screen all changes you did will be lost?")
            },
            confirmButton = {
                TextButton(onClick = {
                    onConfirm()
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    onDismiss()
                }) {
                    Text("No")
                }
            }
        )
    }
}