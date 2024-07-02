package com.android.burakgunduz.bitirmeprojesi.ui.screens.userProfileScreen.subScreens

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.android.burakgunduz.bitirmeprojesi.ui.screens.components.FakeTopBar
import com.android.burakgunduz.bitirmeprojesi.ui.screens.components.getImageUri
import com.android.burakgunduz.bitirmeprojesi.viewModels.AuthViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditUserScreen(viewModel: AuthViewModel, userID: String, navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val showBottomSheet = remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val photoTakenSuccess = remember { mutableStateOf(false) }
    val tempImage = remember { mutableStateOf(Uri.EMPTY) }

    LaunchedEffect(userID) {
        viewModel.getUserProfile(userID)
    }

    val userInfo by viewModel.sellerProfile.observeAsState()
    val userImage by viewModel.sellerImage.observeAsState()

    val userPhoneNumber = remember(userInfo) { mutableStateOf(userInfo?.value?.phoneNumber ?: "") }
    val userName = remember(userInfo) { mutableStateOf(userInfo?.value?.name ?: "") }
    val tempPhoto = remember { mutableStateOf(userImage) }

    LaunchedEffect(userImage) {
        tempPhoto.value = userImage
    }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            tempPhoto.value = it
        }
    }

    val photoTaker = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            tempPhoto.value = tempImage.value
        }
        photoTakenSuccess.value = success
    }

    BackHandler(showBottomSheet.value) {
        coroutineScope.launch { sheetState.hide() }
    }

    LaunchedEffect(photoTakenSuccess.value) {
        if (photoTakenSuccess.value) {
            tempImage.value = tempPhoto.value
            photoTakenSuccess.value = false
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            FakeTopBar(navController = navController, screenName = "Edit Profile")
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box {
                    SubcomposeAsyncImage(
                        model = tempPhoto.value,
                        contentDescription = "ProfilePicture",
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier
                            .padding(10.dp)
                            .size(200.dp)
                            .clip(CircleShape)
                            .clickable { showBottomSheet.value = true }
                    ) {
                        val state = painter.state
                        when (state) {
                            is AsyncImagePainter.State.Loading -> CircularProgressIndicator()
                            is AsyncImagePainter.State.Error -> Icon(
                                imageVector = Icons.Outlined.AccountCircle,
                                contentDescription = "Person"
                            )
                            else -> SubcomposeAsyncImageContent()
                        }
                    }
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier.size(200.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = "",
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
                OutlinedTextField(
                    value = userName.value,
                    onValueChange = { if (it.length <= 25) userName.value = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = AbsoluteRoundedCornerShape(12)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = userPhoneNumber.value,
                    onValueChange = { if (it.length <= 10) userPhoneNumber.value = it },
                    label = { Text("Phone Number") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = AbsoluteRoundedCornerShape(12)
                )
                Spacer(modifier = Modifier.height(16.dp))
                ElevatedButton(onClick = {
                    viewModel.updateUserDetails(userPhoneNumber.value, userName.value) { success, message ->
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }
                    if (tempPhoto.value != userImage) {
                        viewModel.uploadProfilePhoto(userID, tempPhoto.value ?: Uri.EMPTY) { success, message ->
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }
                    }
                    navController.popBackStack()
                }) {
                    Text("Save Changes")
                }
            }
        }
    }


    ProfilePhotoSelectorModalSheet(
        showBottomSheet = showBottomSheet,
        tempPhoto = tempImage,
        context = context,
        galleryLauncher = galleryLauncher,
        photoTaker = photoTaker,
        sheetState = sheetState,
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilePhotoSelectorModalSheet(
    showBottomSheet: MutableState<Boolean>,
    tempPhoto: MutableState<Uri?>,
    context: Context,
    galleryLauncher: ManagedActivityResultLauncher<String, Uri?>,
    photoTaker: ManagedActivityResultLauncher<Uri, Boolean>,
    sheetState: SheetState,
) {
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
                        galleryLauncher.launch("image/*")
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
                        tempPhoto.value = getImageUri(context)
                        photoTaker.launch(tempPhoto.value!!)
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
            }
        }
    }
}

fun photoTaken(
    context: Context,
    photoTakenSuccess: MutableState<Boolean>,
    onePhotoTaker: ManagedActivityResultLauncher<Uri, Boolean>,
    imageUri: MutableState<Uri?>,
    coroutineScope: CoroutineScope,
    tempPhoto: MutableState<Uri?>,
) {
    Log.e("ImageUri", imageUri.toString())
    coroutineScope.launch {
        val takePictureIntent =
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                takePictureIntent.resolveActivity(context.packageManager)?.also {
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri.value)
                }
            }
        onePhotoTaker.contract.parseResult(Activity.RESULT_OK, takePictureIntent)
        tempPhoto.value = imageUri.value
        photoTakenSuccess.value = false
    }
    Log.e("TakePictureIntent", imageUri.toString())
}
