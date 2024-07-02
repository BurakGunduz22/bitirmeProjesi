package com.android.burakgunduz.bitirmeprojesi.ui.screens.userProfileScreen

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.automirrored.outlined.TextSnippet
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.android.burakgunduz.bitirmeprojesi.R
import com.android.burakgunduz.bitirmeprojesi.ui.screens.components.FakeTopBar
import com.android.burakgunduz.bitirmeprojesi.viewModels.ItemViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    navBack: NavBackStackEntry,
    itemViewModel: ItemViewModel,
    storageRef: StorageReference,
    isDarkModeOn: Boolean,
    navController: NavController,
    auth: FirebaseAuth,
    userInfosFar: MutableState<String?>,
) {
    val sellerID = navBack.arguments?.getString("sellerProfileID")

    LaunchedEffect(Unit) {
        itemViewModel.getSellerProfile(sellerID!!)
        itemViewModel.getSellerItems(sellerID)
    }

    val userInfo = itemViewModel.sellerProfile.observeAsState()
    val userImage = itemViewModel.sellerImage.observeAsState()
    val userItems = itemViewModel.itemsOnSale.observeAsState().value
    val coroutineContext = rememberCoroutineScope()
    val realInfo = userInfo.value?.value
    Log.e("SellerProfileScreen", "SellerProfileScreen: ${userImage.value}")

    // New states for managing the bottom sheet and image selection
    val images = remember { mutableStateListOf(Uri.EMPTY) }
    val tempPhoto = remember { mutableStateOf(Uri.EMPTY) }
    val photoTakenSuccess = remember { mutableStateOf(false) }

    val context = LocalContext.current
    val token = stringResource(R.string.default_web_client_id)
    val gso = GoogleSignInOptions
        .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(token)
        .requestEmail()
        .build()
    val googleSignInClient = GoogleSignIn.getClient(context, gso)

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            if (uri != null) {
                images[0] = uri
                // Handle image upload here
            }
        }
    )

    val photoTaker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                images[0] = tempPhoto.value
                photoTakenSuccess.value = true
                // Handle image upload here
            }
        }
    )

    Surface {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                FakeTopBar(
                    navController = navController,
                    screenName = "Your Account",
                    isItLogout = true,
                    onLogout = {
                        coroutineContext.launch {
                            delay(1000)
                            userInfosFar.value = null
                            googleSignInClient.signOut()
                            auth.signOut()
                            navController.navigate("landingPageNav") {
                                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                        Log.e("UserLogOut", "User is logged out:${userInfosFar.value}")
                    }
                )
            }
            SubcomposeAsyncImage(
                model = userImage.value,
                contentDescription = "ProfilePicture",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .padding(10.dp)
                    .size(100.dp)
                    .clip(shape = CircleShape)
                // Show bottom sheet on click
            ) {
                val state = painter.state
                when (state) {
                    is AsyncImagePainter.State.Loading -> {
                        CircularProgressIndicator()
                    }

                    is AsyncImagePainter.State.Empty, is AsyncImagePainter.State.Error -> {
                        Icon(
                            imageVector = Icons.Outlined.AccountCircle,
                            contentDescription = "Person"
                        )
                    }

                    else -> {
                        SubcomposeAsyncImageContent()
                    }
                }
            }

            Text(text = realInfo?.name ?: "Loading...")
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                OutlinedCard(modifier = Modifier.fillMaxSize(0.95f)) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {
                        Spacer(modifier = Modifier.height(10.dp))
                        AccountButtons(
                            onClick = { navController.navigate("editUserPage") },
                            text = "User Information",
                            icon = Icons.Outlined.Person
                        )
                        AccountButtons(onClick = {
                            navController.navigate("userListedItems/$sellerID&${realInfo?.userID}")
                        }, text = "Listed Items", icon = Icons.Outlined.Inventory2)
                        AccountButtons(onClick = {
                            navController.navigate("reportScreen")
                        }, text = "Your reports", icon = Icons.AutoMirrored.Outlined.TextSnippet)
                    }
                }
            }
        }
    }
}


@Composable
fun AccountButtons(onClick: () -> Unit, text: String, icon: ImageVector) {
    ElevatedButton(
        onClick = { onClick() }, modifier = Modifier
            .fillMaxWidth(0.95f)
            .clip(
                AbsoluteRoundedCornerShape(15)
            )
            .height(80.dp)
            .padding(vertical = 10.dp),
        shape = AbsoluteRoundedCornerShape(15)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "Icon",
                modifier = Modifier.padding(end = 5.dp)
            )
            Text(text = text)
        }
    }
}

