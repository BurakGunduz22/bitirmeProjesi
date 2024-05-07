package com.android.burakgunduz.bitirmeprojesi.itemViewModel

import android.content.ContentValues.TAG
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

data class Item(
    var itemName: String = "",
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
    var itemCondition: Int = 0,
    val itemSubCategory: String = "",
    val userID: String = "",
    var itemID: String = ""
    // Add other fields as necessary
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

    fun loadItemDetails(itemID: String) {
        db.collection("itemsOnSale")
            .document(itemID)
            .get()
            .addOnSuccessListener { document ->
                val itemDetail = document.toObject(Item::class.java)?.copy(itemID = document.id)

                itemDetails.value = itemDetail
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }

    fun loadItemImages(itemID: String) {
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

    fun saveItem(fileStream: Uri) {
        val item = Item(
            "item",
            "desc",
            100,
            "brand",
            "category",
            "street",
            "district",
            "town",
            "city",
            "country",
            Timestamp.now(),
            0,
            "subCategory",
            "userID",
            "itemID"
        )
        val itemIDforRef = mutableStateOf("")
        val itemStorageRef = storageRef.child("itemImages")
        db.collection("itemsOnSale")
            .add(item)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                documentReference.update("itemID", documentReference.id)
                itemIDforRef.value = documentReference.id
                itemStorageRef.child("${itemIDforRef.value}/0.png").putFile(fileStream).addOnSuccessListener { _ ->
                    Log.d("OLDUM", "File uploaded successfully")
                }.addOnFailureListener { e ->
                    Log.e("OLMADIM", "Error uploading file", e)
                }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }

    }

}