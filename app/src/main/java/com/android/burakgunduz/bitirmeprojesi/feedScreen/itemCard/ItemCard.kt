package com.android.burakgunduz.bitirmeprojesi.feedScreen.itemCard

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import com.android.burakgunduz.bitirmeprojesi.colors.itemSubTitleTextColorChanger
import com.android.burakgunduz.bitirmeprojesi.colors.itemTitleTextColorChanger
import com.android.burakgunduz.bitirmeprojesi.fonts.robotoFonts
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference


@Composable
fun ItemCard(
    storageRef: StorageReference,
    db: FirebaseFirestore,
    documentId: String,
    imageUrl: String,
    isDarkModeOn: Boolean,
    navController: NavController
) {
    val colorStops = arrayOf(
        0.0f to Color.hsl(0f, 0f, 0f, 0f),
        0.4f to Color.hsl(0f, 0f, 0.1f, 0.1f),
        1f to Color.Black
    )
    val isLoaded = remember { mutableStateOf(false) }
    val dbDetails = db.collection("itemsOnSale").document(documentId)
    val itemPrice = remember { mutableStateOf<Int?>(0) }
    val userDataList =
        remember { mutableListOf<String>("itemName", "itemCategory", "itemTown") }
    LaunchedEffect(dbDetails) {
        dbDetails.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    for (i in userDataList.indices) {
                        userDataList[i] = document.getString(userDataList[i])!!
                        Log.e("ItemCard", "ItemCard: ${userDataList[i]}")
                        isLoaded.value = true
                    }
                    itemPrice.value = document.getDouble("itemPrice")!!.toInt()
                } else {
                    // The document does not exist
                    println("No such document")
                }
            }
            .addOnFailureListener { exception ->
                // An error occurred while retrieving the document
                println("Error getting document: $exception")
            }
    }
    if (isLoaded.value) {
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .size(200.dp, 250.dp)
                .padding(10.dp)
                .clickable { navController.navigate("itemDetailsPageNav/{$documentId}") }
        ) {
            Box(

            ) {
                if (imageUrl.isNotEmpty()) {
                    Log.e("FeedScreen", "FeedScreen: $imageUrl")
                    SubcomposeAsyncImage(
                        model = imageUrl,
                        contentDescription = "Item Image",
                        contentScale = ContentScale.FillWidth
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Brush.verticalGradient(colorStops = colorStops))
                    )
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Bottom,
                        horizontalAlignment = Alignment.Start
                    ) {
                        TitleText(userDataList[0], isDarkModeOn)
                        Row {
                            SubTitletext(userDataList[1], isDarkModeOn, 10)
                            SubTitletext("•", isDarkModeOn, 5)
                            SubTitletext(userDataList[2], isDarkModeOn, 5)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TitleText(titleName: String, isDarkModeOn: Boolean) {
    Text(
        text = titleName,
        fontSize = 20.sp,
        fontFamily = robotoFonts,
        fontWeight = FontWeight.Bold,
        color = itemTitleTextColorChanger(isDarkModeOn = isDarkModeOn),
        modifier = Modifier.padding(top = 60.dp, start = 10.dp)
    )
}

@Composable
fun SubTitletext(subTitleName: String, isDarkModeOn: Boolean, paddingValue: Int) {
    Text(
        text = subTitleName,
        fontSize = 15.sp,
        fontFamily = robotoFonts,
        fontWeight = FontWeight.Medium,
        color = itemSubTitleTextColorChanger(isDarkModeOn = isDarkModeOn),
        modifier = Modifier.padding(bottom = 25.dp, start = paddingValue.dp)
    )
}

