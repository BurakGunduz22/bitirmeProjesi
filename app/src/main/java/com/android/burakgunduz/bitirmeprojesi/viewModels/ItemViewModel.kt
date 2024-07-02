package com.android.burakgunduz.bitirmeprojesi.viewModels

import android.content.ContentValues.TAG
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


data class Item(
    var itemName: String = "",
    val itemDesc: String = "",
    val itemPrice: Int = 0,
    val itemBrand: String = "",
    val itemCategory: String = "",
    val itemLocation: GeoPoint = GeoPoint(0.0, 0.0),
    val itemDate: Timestamp = Timestamp.now(),
    val itemCondition: Int = 0,
    val itemSubCategory: String = "",
    val userID: String = "",
    val itemID: String = "",
    val viewCount: Int = 0,
    val likeCount: Int = 0,
    val itemStatus: Int,
)

data class ItemCard(
    val itemName: String = "",
    val itemPrice: Int = 0,
    val itemCategory: String = "",
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


data class Categories(val categoryName: String, val categoryID: String)


data class SubCategories(val subCategoryName: String, val subCategoryID: String)

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
    val likedItems:MutableLiveData<List<ItemCard>> = MutableLiveData()
    val itemDetails: MutableLiveData<Item?> = MutableLiveData()
    val itemImages: MutableLiveData<List<NamedUri>> = MutableLiveData()
    val sellerProfile = MutableLiveData<MutableState<SellerInfo>>()
    val sellerImage = MutableLiveData<Uri>()
    val itemShowcaseImages = MutableLiveData<List<ItemIDUri>>()
    val categoriesList = MutableLiveData<List<Categories>>()
    val subCategoriesList = MutableLiveData<List<SubCategories>>()
    private val _isDeleting = MutableStateFlow(false)
    val isDeleting: StateFlow<Boolean> = _isDeleting

    var lastVisible: DocumentSnapshot? = null
    private var isLoading = false
    private val loadedItemIds = mutableSetOf<String>()

    fun loadItems(
        startAfter: DocumentSnapshot? = null,
        refresh: Boolean = false,
        stopItem: (Boolean) -> Unit
    ) {
        if (isLoading) return
        isLoading = true

        if (refresh) {
            lastVisible = null
            itemsOnSale.value = emptyList()
            loadedItemIds.clear()
        }

        var query = db.collection("itemsOnSale")
            .limit(5)
            .orderBy("itemDate", Query.Direction.DESCENDING)

        if (startAfter != null) {
            query = query.startAfter(startAfter)
        }

        query.get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    stopItem(true)
                    isLoading = false
                    return@addOnSuccessListener
                }

                val tasks = documents.map { document ->
                    val itemCard =
                        document.toObject(ItemCard::class.java).copy(itemID = document.id)
                    Log.e("nolduLan", itemCard.toString())
                    db.collection("itemCategories")
                        .document(itemCard.itemCategory)
                        .get()
                        .continueWith { task ->
                            if (task.isSuccessful) {
                                val categoryDocument = task.result
                                val categoryName = categoryDocument?.getString("categoryName")
                                itemCard.copy(itemCategory = categoryName ?: "Unknown")
                            } else {
                                itemCard
                            }
                        }
                }

                Tasks.whenAllSuccess<ItemCard>(tasks).addOnSuccessListener { items ->
                    lastVisible = documents.documents.lastOrNull()
                    Log.e("nolduLan", items.toString())
                    val newItems = items.filter { it.itemID !in loadedItemIds }
                    loadedItemIds.addAll(newItems.map { it.itemID })

                    val currentItems = itemsOnSale.value.orEmpty()
                    itemsOnSale.value = currentItems + newItems

                    loadShowcaseImages(newItems) {
                        stopItem(true)
                    }

                    isLoading = false
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
                isLoading = false
            }
    }

    fun loadShowcaseImages(itemsList: List<ItemCard>, stopItem: (Boolean) -> Unit) {
        val tasks = itemsList.map { itemCard ->
            storageRef.child("itemImages/${itemCard.itemID}/0.png").downloadUrl
        }
        Tasks.whenAllSuccess<Uri>(tasks).addOnSuccessListener { uris ->
            val itemIDUris = uris.mapIndexed { index, uri ->
                ItemIDUri(itemsList[index].itemID, uri)
            }
            Log.e("ItemViewModel", "loadShowcaseImages: $itemIDUris")
            itemShowcaseImages.value = (itemShowcaseImages.value ?: emptyList()) + itemIDUris
            Log.e("ItemViewModel", "loadShowcaseImages: ${itemShowcaseImages.value}")
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
                    loadItemCategory(itemCategory ?: "") { categoryName ->
                        Log.e("ItemViewModel", "loadItemDetails: $categoryName")
                        loadSubCategory(
                            itemCategory ?: "",
                            itemSubCategory ?: ""
                        ) { subCategoryName ->
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

    fun loadItemCategory(itemCategory: String, callback: (String) -> Unit) {
        db.collection("itemCategories")
            .document(itemCategory)
            .get()
            .addOnSuccessListener { document ->
                val categories = document.getString("categoryName")
                Log.e("ItemViewModel", "loadItemCategory: $categories")
                return@addOnSuccessListener callback(categories ?: "Place Holder")

            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }

    fun loadItemCategories() {
        db.collection("itemCategories")
            .get()
            .addOnSuccessListener { documents ->
                Log.e("ItemViewModel", "loadItemCategories: $documents")
                val categoryList = documents.mapNotNull { document ->
                    Log.e("ItemViewModel", "loadItemCategories: ${document.id}")
                    Categories(document.getString("categoryName") ?: "", document.id)
                }
                categoriesList.value = categoryList
                Log.e("ItemViewModel", "loadItemCategories: ${categoryList.size}")
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }

    fun loadSubItemCategories(itemCategory: String) {
        db.collection("itemCategories")
            .document(itemCategory)
            .collection("subCategories")
            .get()
            .addOnSuccessListener { documents ->
                Log.e("ItemViewModel", "loadItemCategories: $documents")
                val categoryList = documents.mapNotNull { document ->
                    Log.e("ItemViewModel", "loadItemCategories: ${document.id}")
                    SubCategories(document.getString("subCategoryName") ?: "", document.id)
                }
                subCategoriesList.value = categoryList
                Log.e("ItemViewModel", "loadItemCategories: ${categoryList.size}")
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }

    fun loadSubCategory(itemCategory: String, itemSubCategory: String, callback: (String) -> Unit) {
        db.collection("itemCategories")
            .document(itemCategory)
            .collection("subCategories")
            .document(itemSubCategory)
            .get()
            .addOnSuccessListener { documents ->
                val subCategories = documents.getString("subCategoryName")
                Log.e("ItemViewModel", "loadSubCategories: $subCategories")
                return@addOnSuccessListener callback(subCategories ?: "Place Holder")
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }

    fun loadItemImages(itemID: String, isItemSame: Boolean) {
        if (!isItemSame) {
            val itemImageRef = storageRef.child("itemImages/$itemID")
            itemImageRef.listAll().addOnSuccessListener { listResult ->
                val namedUriList = mutableListOf<NamedUri>()
                for (item in listResult.items) {
                    item.downloadUrl.addOnSuccessListener { uri ->
                        val namedUri = NamedUri(item.name.split(".").first(), uri)
                        namedUriList.add(namedUri)
                    }.addOnCompleteListener {
                        // When all downloadUrl tasks are complete, update itemImages.value
                        if (namedUriList.size == listResult.items.size) {
                            itemImages.value = namedUriList.sortedBy { it.name.toInt() }
                        }
                    }
                }
            }
        } else {
            Log.d("ItemViewModel", "loadItemImages: Images already loaded")
        }
    }

    fun loadShowCaseImage(itemID: String, callback: (Uri) -> Unit) {
        val itemImageRef = storageRef.child("itemImages/$itemID/0.png")
        itemImageRef.downloadUrl.addOnSuccessListener { uri ->
            callback(uri)
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
                        Log.d("SellerImage", "SellerImage: $it")
                        Log.d("SellerProfile", "SellerProfile: ${sellerProfile.value!!.value}")
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
                val tasks = documents.documents.map { document ->
                    val itemCard =
                        document.toObject(ItemCard::class.java)?.copy(itemID = document.id)
                    Log.e("nolduLan", itemCard.toString())
                    db.collection("itemCategories")
                        .document(itemCard?.itemCategory ?: "")
                        .get()
                        .continueWith { task ->
                            if (task.isSuccessful) {
                                val categoryDocument = task.result
                                val categoryName = categoryDocument?.getString("categoryName")
                                itemCard?.copy(itemCategory = categoryName ?: "Unknown")
                            } else {
                                itemCard
                            }
                        }
                }
                Tasks.whenAllSuccess<ItemCard>(tasks).addOnSuccessListener { items ->
                    itemsOnSale.value = items.filterNotNull()
                }
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

    private val likedItemsCache = mutableStateOf<Map<String, Boolean>>(emptyMap())

    fun loadLikedItems(userID: String, callback: () -> Unit) {
        db.collection("users")
            .document(userID)
            .collection("likedItems")
            .get()
            .addOnSuccessListener { documents ->
                val likedItemsMap = documents.associate { it.id to true }
                likedItemsCache.value = likedItemsMap
                callback()
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting liked items: ", exception)
            }
    }

    fun isItemLiked(itemID: String): Boolean {
        return likedItemsCache.value[itemID] == true
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
                        .continueWith { task ->
                            if (task.isSuccessful) {
                                val itemDocument = task.result
                                val itemCard = itemDocument?.toObject(ItemCard::class.java)
                                    ?.copy(itemID = document.id)
                                itemCard
                            } else {
                                null
                            }
                        }
                        .continueWithTask { task ->
                            val itemCard = task.result
                            if (itemCard != null) {
                                db.collection("itemCategories")
                                    .document(itemCard.itemCategory)
                                    .get()
                                    .continueWith { categoryTask ->
                                        if (categoryTask.isSuccessful) {
                                            val categoryDocument = categoryTask.result
                                            val categoryName =
                                                categoryDocument?.getString("categoryName")
                                            itemCard.copy(itemCategory = categoryName ?: "Unknown")
                                        } else {
                                            itemCard
                                        }
                                    }
                            } else {
                                Tasks.forResult(null)
                            }
                        }
                }
                Tasks.whenAllSuccess<ItemCard>(tasks).addOnSuccessListener { items ->
                    likedItems.value = items.filterNotNull() // Assuming `likedItems` is the MutableLiveData for liked items
                    Log.e("LikedItems", likedItems.value.toString())
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
                "itemSubCategory", itemUpdatedDetails[3],
                "itemDesc", itemUpdatedDetails[4],
                "itemPrice", itemUpdatedDetails[5].toInt(),
                "itemCondition", itemUpdatedDetails[6].toInt(),
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
            itemSubCategory = itemDetails[3],
            itemDesc = itemDetails[4],
            itemPrice = itemDetails[5].toInt(),
            itemCondition = itemDetails[6].toInt(),
            itemLocation = GeoPoint(itemDetails[7].toDouble(), itemDetails[8].toDouble()),
            itemDate = Timestamp.now(),
            itemStatus = 0,
            itemID = "",
            userID = userID,
            viewCount = 0,
            likeCount = 0
        )
    }

    fun getItemName(itemID: String, itemName: (String) -> Unit) {
        db.collection("itemsOnSale")
            .document(itemID)
            .get()
            .addOnSuccessListener { document ->
                document.getString("itemName")?.let { itemName(it) }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }

    fun getUserIDFromItemID(itemID: String, callback: (String?) -> Unit) {
        db.collection("itemsOnSale")
            .document(itemID)
            .get()
            .addOnSuccessListener { document ->
                val userID = document.getString("userID")
                callback(userID)
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting userID from itemID: $itemID", exception)
                callback(null)
            }
    }

    private val _items = MutableStateFlow<List<ItemCard>>(emptyList())
    val items: StateFlow<List<ItemCard>> get() = _items
    fun deleteItem(itemID: String) {
        // Delete item document from 'itemsOnSale' collection
        viewModelScope.launch {
            _isDeleting.value = true
            try {
                db.collection("itemsOnSale")
                    .document(itemID)
                    .delete()
                    .addOnSuccessListener {
                        Log.d(TAG, "Item document successfully deleted!")

                        // Remove the item from all users' liked items
                        db.collection("users")
                            .get()
                            .addOnSuccessListener { users ->
                                val deleteLikedItemsTasks = users.documents.map { userDoc ->
                                    db.collection("users")
                                        .document(userDoc.id)
                                        .collection("likedItems")
                                        .document(itemID)
                                        .delete()
                                        .addOnSuccessListener {
                                            Log.d(
                                                TAG,
                                                "Liked item document successfully deleted for user: ${userDoc.id}"
                                            )
                                        }
                                        .addOnFailureListener { e ->
                                            Log.w(
                                                TAG,
                                                "Error deleting liked item document for user: ${userDoc.id}",
                                                e
                                            )
                                        }
                                }

                                Tasks.whenAll(deleteLikedItemsTasks).addOnSuccessListener {
                                    Log.d(TAG, "All liked item documents successfully deleted!")

                                    // Delete all messages related to the item from 'messages' collection
                                    db.collection("messages")
                                        .whereEqualTo("itemID", itemID)
                                        .get()
                                        .addOnSuccessListener { messages ->
                                            val deleteMessagesTasks =
                                                messages.documents.map { messageDoc ->
                                                    messageDoc.reference.delete()
                                                        .addOnSuccessListener {
                                                            Log.d(
                                                                TAG,
                                                                "Message document successfully deleted: ${messageDoc.id}"
                                                            )
                                                        }
                                                        .addOnFailureListener { e ->
                                                            Log.w(
                                                                TAG,
                                                                "Error deleting message document: ${messageDoc.id}",
                                                                e
                                                            )
                                                        }
                                                }
                                            Tasks.whenAll(deleteMessagesTasks)
                                                .addOnSuccessListener {

                                                    // Delete all item images from Firebase Storage
                                                    val itemImagesRef =
                                                        storageRef.child("itemImages/$itemID")
                                                    itemImagesRef.listAll()
                                                        .addOnSuccessListener { listResult ->
                                                            val deleteImageTasks =
                                                                listResult.items.map { imageRef ->
                                                                    imageRef.delete()
                                                                        .addOnSuccessListener {
                                                                            Log.d(
                                                                                TAG,
                                                                                "Image successfully deleted: ${imageRef.path}"
                                                                            )
                                                                        }
                                                                        .addOnFailureListener { e ->
                                                                            Log.w(
                                                                                TAG,
                                                                                "Error deleting image: ${imageRef.path}",
                                                                                e
                                                                            )
                                                                        }
                                                                }

                                                            Tasks.whenAll(deleteImageTasks)
                                                                .addOnSuccessListener {
                                                                    Log.d(
                                                                        TAG,
                                                                        "All item images successfully deleted!"
                                                                    )
                                                                }
                                                        }
                                                        .addOnFailureListener { e ->
                                                            Log.w(
                                                                TAG,
                                                                "Error listing item images for deletion",
                                                                e
                                                            )
                                                        }
                                                    Log.d(
                                                        TAG,
                                                        "All message documents successfully deleted!"
                                                    )
                                                }
                                        }
                                        .addOnFailureListener { e ->
                                            Log.w(TAG, "Error getting messages for deletion", e)
                                        }
                                }
                            }
                            .addOnFailureListener { e ->
                                Log.w(TAG, "Error getting users for liked items", e)
                            }
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error deleting item document", e)
                    }
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting item", e)
            } finally {
                _isDeleting.value = false
            }
        }
    }

    suspend fun searchItemsIn(
        query: String,
        category: Categories?,
        subCategory: SubCategories?,
        callback: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            _items.value = searchItems(query, category, subCategory) {
                callback(it)
            }
        }
    }

    suspend fun searchItems(
        query: String,
        category: Categories?,
        subCategory: SubCategories?,
        callback: (Boolean) -> Unit
    ): List<ItemCard> {
        return try {
            // Fetch all items from the Firestore database
            val allItems = db.collection("itemsOnSale")
                .get()
                .await()
                .documents
                .map { document ->
                    val itemCard =
                        document.toObject(ItemCard::class.java)?.copy(itemID = document.id)
                    itemCard
                }

            // Fetch category names for each item
            val tasks = allItems.map { itemCard ->
                db.collection("itemCategories")
                    .document(itemCard?.itemCategory ?: "")
                    .get()
                    .continueWith { task ->
                        if (task.isSuccessful) {
                            val categoryDocument = task.result
                            val categoryName = categoryDocument?.getString("categoryName")
                            itemCard?.copy(itemCategory = categoryName ?: "Unknown")
                        } else {
                            itemCard
                        }
                    }
            }

            val itemsWithCategories = Tasks.whenAllSuccess<ItemCard>(tasks).await()

            callback(true)

            // Filter items locally based on the query, category, and subcategory
            itemsWithCategories.filter { item ->
                item.itemName.contains(query, ignoreCase = true) &&
                        (category == null || item.itemCategory == category.categoryName) &&
                        (subCategory == null || item.itemSubCategory == subCategory.subCategoryID)
            }
        } catch (e: Exception) {
            emptyList<ItemCard>()
        }
    }
    fun replaceItemImages(itemID: String, newImageUris: List<Uri>) {
        val itemImagesRef = storageRef.child("itemImages/$itemID")

        // Retrieve the download URLs of existing images
        itemImagesRef.listAll()
            .addOnSuccessListener { listResult ->
                val downloadUrlTasks = listResult.items.map { imageRef ->
                    imageRef.downloadUrl
                        .addOnSuccessListener { uri ->
                            Log.d(TAG, "Existing image download URL: $uri")
                        }
                        .addOnFailureListener { e ->
                            Log.w(TAG, "Error getting download URL for image: ${imageRef.path}", e)
                        }
                }

                // Wait for all download URLs to be retrieved
                Tasks.whenAllSuccess<Uri>(downloadUrlTasks).addOnSuccessListener { existingImageUris ->
                    val existingImageUrisSet = existingImageUris.toSet()
                    val newImageUrisSet = newImageUris.toSet()

                    // Find images to upload
                    val imagesToUpload = newImageUrisSet - existingImageUrisSet

                    // Upload new images
                    imagesToUpload.forEachIndexed { index, uri ->
                        // Ensure the index matches the new image position
                        val storageUri = itemImagesRef.child("$index.png")

                        // Check if the image already exists before uploading
                        storageUri.downloadUrl
                            .addOnSuccessListener { existingUri ->
                                if (uri != existingUri) {
                                    storageUri.putFile(uri)
                                        .addOnSuccessListener {
                                            Log.d("ImageUpload", "New image uploaded successfully")
                                        }
                                        .addOnFailureListener { e ->
                                            Log.e("ImageUpload", "Error uploading new image", e)
                                        }
                                        .addOnProgressListener { taskSnapshot ->
                                            val progress =
                                                (100.0 * taskSnapshot.bytesTransferred) / taskSnapshot.totalByteCount
                                            Log.d("ImageUploadProgress", "Upload is $progress% done")
                                        }
                                } else {
                                    Log.d("ImageUpload", "Image already exists, skipping upload")
                                }
                            }
                            .addOnFailureListener { e ->
                                Log.w("ImageUpload", "Error getting existing image download URL: ${storageUri.path}", e)
                            }
                    }
                    Log.d(TAG, "Checked and uploaded new images if needed")
                }.addOnFailureListener { e ->
                    Log.w(TAG, "Error retrieving download URLs", e)
                }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error listing images", e)
            }
    }



}