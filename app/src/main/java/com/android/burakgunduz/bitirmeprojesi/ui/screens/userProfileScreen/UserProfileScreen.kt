package com.android.burakgunduz.bitirmeprojesi.ui.screens.userProfileScreen

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.android.burakgunduz.bitirmeprojesi.ui.screens.components.UserListedItemCard
import com.android.burakgunduz.bitirmeprojesi.viewModels.ItemViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
    Surface {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SubcomposeAsyncImage(
                model = userImage.value,
                contentDescription = "ProfilePicture",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .padding(10.dp)
                    .size(100.dp)
                    .clip(shape = CircleShape)

            ) {
                val state = painter.state
                when (state) {
                    is AsyncImagePainter.State.Loading -> {
                        CircularProgressIndicator()
                    }

                    is AsyncImagePainter.State.Empty, is AsyncImagePainter.State.Error -> {
                        Icon(imageVector = Icons.Outlined.AccountCircle, contentDescription = "Person")
                    }

                    else -> {
                        SubcomposeAsyncImageContent()
                    }
                }
            }
            val context = LocalContext.current
            val token = stringResource(R.string.default_web_client_id)
            val gso = GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(token)
                .requestEmail()
                .build()
            val googleSignInClient = GoogleSignIn.getClient(context, gso)
            Text(text = realInfo?.name ?: "Loading...")
            Text(text = realInfo?.email ?: "Loading...")
            Text(text = realInfo?.phoneNumber ?: "Loading...")
            IconButton(onClick = {
                navController.navigate("landingPageNav")
                coroutineContext.launch {
                    delay(1000)
                    userInfosFar.value = null
                    googleSignInClient.signOut()
                    auth.signOut()
                }
                Log.e("UserLogOut", "User is logged out:${userInfosFar.value}")
            })
            {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.Logout,
                    contentDescription = "Logout",
                    modifier = Modifier.size(25.dp),
                )
            }
            if (userItems != null) {
                LazyColumn(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    itemsIndexed(userItems) { _, document ->
                        val imageUrl = remember { mutableStateOf("") }
                        LaunchedEffect(document) {
                            val storageRefer =
                                storageRef.child("/itemImages/${document.itemID}/0.png")
                            storageRefer.downloadUrl.addOnSuccessListener {
                                imageUrl.value = it.toString()
                            }
                        }
                        if (imageUrl.value != "") {
                            UserListedItemCard(
                                document,
                                imageUrl.value,
                                isDarkModeOn,
                                navController
                            )
                        } else {
                            // Show a loading indicator while the image is loading
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }
}