package com.android.burakgunduz.bitirmeprojesi.ViewModels

import android.content.ContentValues.TAG
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

data class Item(
    val itemName: String = "",
    val itemDesc: String = "",
    val itemPrice: Int = 0,
    val itemBrand: String = "",
    val itemCategory: String = "",
    val itemStreet: String = "",
    val itemDistrict: String = "",
    val itemTown: String = "",
    val itemCity: String = "",
    val itemCountry: String = "",
    val itemDate: Timestamp = Timestamp.now(),
    val itemCondition: Int = 0,
    val itemSubCategory: String = "",
    val userID: String = "",
    val itemID: String = ""
)

data class ItemCard(
    val itemName: String = "",
    val itemPrice: Int = 0,
    val itemCategory: String = "",
    val itemTown: String = "",
    val itemDate: Timestamp = Timestamp.now(),
    val itemCondition: Int = 0,
    val itemSubCategory: String = "",
    val userID: String = "",
    val itemID: String = ""
)

data class NamedUri(val name: String, val uri: Uri)
class ItemViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val fireStorageDB = Firebase.storage("gs://bitirmeproje-ad56d.appspot.com")
    private val storageRef = fireStorageDB.reference
    val itemsOnSale: MutableLiveData<List<ItemCard>> by lazy {
        MutableLiveData<List<ItemCard>>().also {
            loadItems()
        }
    }
    val itemDetails: MutableLiveData<Item?> = MutableLiveData()
    val itemImages: MutableLiveData<List<NamedUri>> = MutableLiveData()
    fun loadItems() {
        db.collection("itemsOnSale")
            .get()
            .addOnSuccessListener { documents ->
                val itemsList = documents.mapNotNull { document ->
                    document.toObject(ItemCard::class.java).copy(itemID = document.id)
                }
                itemsOnSale.value = itemsList
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }

    fun loadItemDetails(itemID: String, isItemSame: Boolean) {
        val itemTemp = itemDetails.value
        Log.e("ItemViewModel", "loadItemDetails: $itemTemp")
            if (!isItemSame) {
                db.collection("itemsOnSale")
                    .document(itemID)
                    .get()
                    .addOnSuccessListener { document ->
                        val itemDetail =
                            document.toObject(Item::class.java)?.copy(itemID = document.id)
                        itemDetails.value = itemDetail
                    }
                    .addOnFailureListener { exception ->
                        Log.w(TAG, "Error getting documents: ", exception)
                    }
            } else {
                Log.d("ItemViewModel", "loadItemDetails: Item already loaded")
            }
    }

    fun loadItemImages(itemID: String, isItemSame: Boolean) {
        if (!isItemSame) {
            itemImages.value = emptyList()
            val itemImageRef = storageRef.child("itemImages/$itemID")
            itemImageRef.listAll().addOnSuccessListener { listResult ->
                listResult.items.forEach { item ->
                    item.downloadUrl.addOnSuccessListener { uri ->
                        val namedUri = NamedUri(item.name.split(".").first(), uri)
                        val currentList = itemImages.value ?: emptyList()
                        val updatedList = currentList + namedUri
                        itemImages.value = updatedList.sortedBy { it.name.toInt() }
                    }
                }
            }
        }
        else{
            Log.d("ItemViewModel", "loadItemImages: Images already loaded")
        }
    }

    fun saveItem(fileStream: MutableState<List<Uri>>, itemDetails: MutableList<String>) {
        val item = createItemFromDetails(itemDetails)
        val itemIDforRef = mutableStateOf("")
        val itemStorageRef = storageRef.child("itemImages")
        db.collection("itemsOnSale")
            .add(item)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                documentReference.update("itemID", documentReference.id)
                itemIDforRef.value = documentReference.id
                fileStream.value.forEachIndexed { index, uri ->
                    itemStorageRef.child("${itemIDforRef.value}/$index.png").putFile(uri)
                        .addOnSuccessListener { _ ->
                            Log.d("OLDUM", "File uploaded successfully")
                        }.addOnFailureListener { e ->
                            Log.e("OLMADIM", "Error uploading file", e)
                        }.addOnProgressListener { taskSnapshot ->
                            val progress =
                                (100.0 * taskSnapshot.bytesTransferred) / taskSnapshot.totalByteCount
                            Log.d("PROGRESS", "Upload is $progress% done")
                        }
                }

            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }

    }

    private fun createItemFromDetails(itemDetails: MutableList<String>): Item {
        return Item(
            itemName = itemDetails[0],
            itemBrand = itemDetails[1],
            itemCategory = itemDetails[2],
            itemDesc = itemDetails[3],
            itemPrice = itemDetails[4].toInt(),
            itemCondition = itemDetails[5].toInt(),
            itemCountry = itemDetails[6],
            itemCity = itemDetails[7],
            itemTown = itemDetails[8],
            itemDistrict = itemDetails[9],
            itemStreet = itemDetails[10],
            itemSubCategory = "",
            itemDate = Timestamp.now(),
            itemID = "",
            userID = "1"
        )
    }

}