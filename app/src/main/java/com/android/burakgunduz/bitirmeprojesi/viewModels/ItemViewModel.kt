package com.android.burakgunduz.bitirmeprojesi.viewModels

import android.content.ContentValues.TAG
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
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
    val itemCondition: Int = 0,
    val itemSubCategory: String = "",
    val userID: String = "",
    val itemID: String = "",
    val viewCount: Int = 0,
    val likeCount: Int = 0,
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
    val itemID: String = "",
    val viewCount: Int = 0,
    val likeCount: Int = 0,
)

data class SellerInfo(
    val name: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val userID: String = ""
)

data class NamedUri(val name: String, val uri: Uri)
data class ItemIDUri(val itemID: String, val uri: Uri)
data class Categories(val categoryName: String,val categoryID: String)
data class SubCategories(val subCategoryName: String,val subCategoryID: String )

class ItemViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val fireStorageDB = Firebase.storage("gs://bitirmeproje-ad56d.appspot.com")
    private val storageRef = fireStorageDB.reference
    val itemsOnSale: MutableLiveData<List<ItemCard>> by lazy {
        MutableLiveData<List<ItemCard>>().also {
            loadItems {
            }
        }
    }
    val itemDetails: MutableLiveData<Item?> = MutableLiveData()
    val itemImages: MutableLiveData<List<NamedUri>> = MutableLiveData()
    val sellerProfile = MutableLiveData<MutableState<SellerInfo>>()
    val sellerImage = MutableLiveData<Uri>()
    val itemShowcaseImages = MutableLiveData<List<ItemIDUri>>()
    val categoriesList = MutableLiveData<List<Categories>>()
    val subCategoriesList = MutableLiveData<List<SubCategories>>()
    fun loadItems(stopItem: (Boolean) -> Unit) {
        db.collection("itemsOnSale")
            .limit(15)
            .get()
            .addOnSuccessListener { documents ->
                val itemsList = documents.mapNotNull { document ->
                    document.toObject(ItemCard::class.java).copy(itemID = document.id)
                }
                itemsOnSale.value = itemsList
                stopItem(true)
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }

    fun loadShowcaseImages(itemsList: List<ItemCard>, stopItem: (Boolean) -> Unit) {
        val tasks = itemsList.map { itemCard ->
            storageRef.child("/itemImages/${itemCard.itemID}/0.png").downloadUrl
        }
        Tasks.whenAllSuccess<Uri>(tasks).addOnSuccessListener { uris ->
            val itemIDUris = uris.mapIndexed { index, uri ->
                ItemIDUri(itemsList[index].itemID, uri)
            }
            itemShowcaseImages.value = itemIDUris
            stopItem(true)
        }
    }

    fun loadItemDetails(itemID: String, isItemSame: Boolean) {
        val itemTemp = itemDetails.value
        val itemRef = db.collection("itemsOnSale").document(itemID)

        Log.e("ItemViewModel", "loadItemDetails: $itemTemp")
        if (!isItemSame) {
            itemRef.get()
                .addOnSuccessListener { document ->
                    val itemCategory = document.getString("itemCategory")
                    val itemSubCategory = document.getString("itemSubCategory")
                    Log.e("ItemViewModel", "loadItemDetails: $itemCategory")
                    loadItemCategory(itemCategory?:"") { categoryName->
                        Log.e("ItemViewModel", "loadItemDetails: $categoryName")
                        loadSubCategory(itemCategory?:"", itemSubCategory?:""){ subCategoryName->
                            Log.e("ItemViewModel", "loadItemDetails: $subCategoryName")
                            val itemDetail =
                                document.toObject(Item::class.java)?.copy(
                                    itemID = document.id,
                                    itemCategory = categoryName,
                                    itemSubCategory = subCategoryName
                                )
                            Log.e("ItemViewModel", "loadItemDetails: $itemDetail")
                            itemDetails.value = itemDetail
                            if (itemDetails.value != null) {
                                getSellerProfile(itemDetails.value!!.userID)
                            }
                        }

                    }
                    itemRef.update("viewCount", FieldValue.increment(1))
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }

        } else {
            Log.d("ItemViewModel", "loadItemDetails: Item already loaded")
        }
    }
    private fun loadItemCategory(itemCategory: String, callback: (String) -> Unit){
        db.collection("itemCategories")
            .document(itemCategory)
            .get()
            .addOnSuccessListener { document ->
                val categories = document.getString("categoryName")
                Log.e("ItemViewModel", "loadItemCategory: $categories")
                return@addOnSuccessListener callback(categories?:"Place Holder")

            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }
    fun loadItemCategories(){
        db.collection("itemCategories")
            .get()
            .addOnSuccessListener { documents ->
                Log.e("ItemViewModel", "loadItemCategories: $documents")
                val categoryList = documents.mapNotNull { document ->
                    Log.e("ItemViewModel", "loadItemCategories: ${document.id}")
                    Categories(document.getString("categoryName")?:"", document.id)
                }
                categoriesList.value = categoryList
                Log.e("ItemViewModel", "loadItemCategories: ${categoryList.size}")
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }
    fun loadSubItemCategories(itemCategory: String){
        db.collection("itemCategories")
            .document(itemCategory)
            .collection("subCategories")
            .get()
            .addOnSuccessListener { documents ->
                Log.e("ItemViewModel", "loadItemCategories: $documents")
                val categoryList = documents.mapNotNull { document ->
                    Log.e("ItemViewModel", "loadItemCategories: ${document.id}")
                    SubCategories(document.getString("subCategoryName")?:"", document.id)
                }
                subCategoriesList.value = categoryList
                Log.e("ItemViewModel", "loadItemCategories: ${categoryList.size}")
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }
    private fun loadSubCategory(itemCategory: String, itemSubCategory: String, callback: (String) -> Unit){
        db.collection("itemCategories")
            .document(itemCategory)
            .collection("subCategories")
            .document(itemSubCategory)
            .get()
            .addOnSuccessListener { documents ->
                val subCategories = documents.getString("subCategoryName")
                Log.e("ItemViewModel", "loadSubCategories: $subCategories")
                return@addOnSuccessListener callback(subCategories?:"Place Holder")
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
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
        } else {
            Log.d("ItemViewModel", "loadItemImages: Images already loaded")
        }
    }

    fun getSellerProfile(userID: String) {
        val itemStorageRef = storageRef.child("userProfileImages")
        db.collection("users")
            .document(userID)
            .get()
            .addOnSuccessListener { document ->
                val userProfile =
                    document.toObject(SellerInfo::class.java)?.copy(userID = document.id)
                Log.d(TAG, "DocumentSnapshot data: $userProfile")
                sellerProfile.value = mutableStateOf(userProfile!!)
                sellerImage.value = "".toUri()
                itemStorageRef.child("$userID/1.png").downloadUrl
                    .addOnSuccessListener {
                        sellerImage.value = it
                    }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
                Log.d(TAG, "DocumentSnapshot data: ${sellerProfile.value}")
            }
    }

    fun getSellerItems(userID: String) {
        db.collection("itemsOnSale")
            .whereEqualTo("userID", userID)
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

    fun saveItem(
        fileStream: MutableList<Uri>,
        itemDetails: MutableList<String>,
        userID: String
    ) {
        val item = createItemFromDetails(itemDetails, userID)
        val itemIDforRef = mutableStateOf("")
        val itemStorageRef = storageRef.child("itemImages")
        db.collection("itemsOnSale")
            .add(item)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                documentReference.update("itemID", documentReference.id)
                itemIDforRef.value = documentReference.id
                fileStream.forEachIndexed { index, uri ->
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

    fun addLikedItem(userID: String, itemID: String) {
        db.collection("itemsOnSale")
            .document(itemID)
            .update("likeCount", FieldValue.increment(1))
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully updated!")
            }
            .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }
        db.collection("users")
            .document(userID)
            .collection("likedItems")
            .document(itemID)
            .set(mapOf("itemID" to itemID))  // Change this line
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully updated!")
            }
            .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }
    }

    fun checkItemIsLiked(userID: String, itemID: String, itemLiked: (Boolean) -> Unit) {
        db.collection("users")
            .document(userID)
            .collection("likedItems")
            .whereEqualTo("itemID", itemID)
            .get()
            .addOnSuccessListener {
                Log.e("nolduLanDayi", it.documents.toString())
                Log.d(TAG, "Checked")
                if (it.documents.toString() != "[]") {
                    itemLiked(true)
                } else {
                    itemLiked(false)
                }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error updating document", e)
                itemLiked(false)
            }
    }

    fun checkLikedItems(userID: String, isItemLoaded: (Boolean) -> Unit) {
        val likedItemList = mutableListOf<ItemCard>()
        db.collection("users")
            .document(userID)
            .collection("likedItems")
            .get()
            .addOnSuccessListener { documents ->
                Log.e("ItemVarMi", documents.documents.toString())
                val tasks = documents.documents.map { document ->
                    Log.e("ItemVarMiLan", document.id)
                    db.collection("itemsOnSale")
                        .document(document.id)
                        .get()
                        .addOnSuccessListener { documents ->
                            val itemsList =
                                documents.toObject(ItemCard::class.java)?.copy(itemID = document.id)
                            likedItemList.add(itemsList!!)
                        }
                        .addOnFailureListener { exception ->
                            Log.w(TAG, "Error getting documents: ", exception)
                        }
                }
                Tasks.whenAllSuccess<Uri>(tasks).addOnSuccessListener {
                    itemsOnSale.value = likedItemList
                    Log.e("LikedItems", likedItemList.toString())
                    isItemLoaded(true)
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }

    fun removeLikedItems(userID: String, itemID: String) {
        db.collection("itemsOnSale")
            .document(itemID)
            .update("likeCount", FieldValue.increment(-1))
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully updated!")
            }
            .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }
        db.collection("users")
            .document(userID)
            .collection("likedItems")
            .document(itemID)
            .delete()
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully deleted!")
            }
            .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
    }

    fun updateItemDetails(itemUpdatedDetails: MutableList<String>, itemID: String) {
        db.collection("itemsOnSale")
            .document(itemID)
            .update(
                "itemName", itemUpdatedDetails[0],
                "itemBrand", itemUpdatedDetails[1],
                "itemCategory", itemUpdatedDetails[2],
                "itemDesc", itemUpdatedDetails[3],
                "itemPrice", itemUpdatedDetails[4].toInt(),
                "itemCondition", itemUpdatedDetails[5].toInt(),
            )
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully updated!")
            }
            .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }
    }

    private fun createItemFromDetails(itemDetails: MutableList<String>, userID: String): Item {
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
            userID = userID,
            viewCount = 0,
            likeCount = 0
        )
    }

}