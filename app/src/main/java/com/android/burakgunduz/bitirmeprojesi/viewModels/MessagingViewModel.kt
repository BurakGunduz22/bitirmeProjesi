package com.android.burakgunduz.bitirmeprojesi.viewModels

import android.content.ContentValues.TAG
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

data class Message(
    val senderID: String = "",
    val receiverID: String = "",
    val itemID: String = "",
    val message: String = "",
    val timestamp: Timestamp = Timestamp.now(),
)

class MessageViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val fireStorageDB = Firebase.storage("gs://bitirmeproje-ad56d.appspot.com")
    val messageList: MutableLiveData<List<Message>> by lazy {
        MutableLiveData<List<Message>>().also {
            getAllMessages("userID")
        }
    }
    val directMessages: MutableLiveData<List<Message>> = MutableLiveData()

    fun sendFirstMessage(
        userID: String,
        conversationUserID: String,
        itemID: String,
        messageContext: String
    ) {
        val sendingMessage = Message(
            senderID = userID,
            receiverID = conversationUserID,
            itemID = itemID,
            message = messageContext
        )
        Log.d("First Message", "Messages: $sendingMessage")
        db.collection("messages")
            .add(sendingMessage).addOnSuccessListener {
                Log.d("First Message", "Messages: $it")
                db.collection("messages")
                    .document(it.id)
                    .collection("messageContext")
                    .add(sendingMessage).addOnSuccessListener {
                        val itemRef = db.collection("itemsOnSale").document(itemID)
                        itemRef.get().addOnSuccessListener { document ->
                            Log.e("First Message", "Document: $document")
                            if (document.exists()) {
                                val currentCount = document.getLong("messageCounter") ?: 0L
                                itemRef.update("messageCounter", currentCount + 1)
                                    .addOnSuccessListener {
                                        Log.d(
                                            "First Message",
                                            "Message counter incremented successfully."
                                        )
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e("First Message", "Error incrementing message counter.", e)
                                    }
                            }
                            else {
                                Log.e("First Message", "Document does not exist.")}
                        }
                    }

            }
    }

    fun sendMessage(
        userID: String,
        conversationUserID: String,
        itemID: String,
        messageContext: String
    ) {
        val sendingMessage = Message(
            senderID = userID,
            receiverID = conversationUserID,
            itemID = itemID,
            message = messageContext
        )
        Log.d("New Message", "Messages: $sendingMessage")
        db.collection("messages")
            .whereEqualTo("itemID", itemID)
            .whereIn("senderID", listOf(userID, conversationUserID))
            .whereIn("receiverID", listOf(userID, conversationUserID))
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.documents.isNotEmpty()) {
                    db.collection("messages")
                        .document(snapshot.documents[0].id)
                        .collection("messageContext")
                        .add(sendingMessage)
                    // Increment the messageCounter in itemsOnSale collection
                    val itemRef = db.collection("itemsOnSale").document(itemID)
                    itemRef.get().addOnSuccessListener { document ->
                        if (document.exists()) {
                            val currentCount = document.getLong("messageCounter") ?: 0L
                            itemRef.update("messageCounter", currentCount + 1)
                                .addOnSuccessListener {
                                    Log.d(
                                        "First Message",
                                        "Message counter incremented successfully."
                                    )
                                }
                                .addOnFailureListener { e ->
                                    Log.e("First Message", "Error incrementing message counter.", e)
                                }
                        }
                    }
                } else {
                    db.collection("messages")
                        .add(sendingMessage).addOnSuccessListener {
                            db.collection("messages")
                                .document(it.id)
                                .collection("messageContext")
                                .add(sendingMessage)
                            Log.e("First Message", "Messages: $it")
                        }
                }
            }
    }


    fun getDirectMessages(userID: String, itemID: String, receiverID: String,messagesLoaded:(Boolean)->Unit) {
        Log.d("MesajlarGeldi", "Messages: $userID, $itemID, $receiverID")

        db.collection("messages")
            .whereEqualTo("itemID", itemID)
            .whereIn("senderID", listOf(userID, receiverID))
            .whereIn("receiverID", listOf(userID, receiverID))
            .get()
            .addOnSuccessListener { result ->
                if (result != null && result.documents.isNotEmpty()) {
                    val messageDocId = result.documents[0].id
                    db.collection("messages").document(messageDocId)
                        .collection("messageContext")
                        .orderBy("timestamp", Query.Direction.ASCENDING)
                        .addSnapshotListener { documents, error ->
                            if (error != null) {
                                Log.w(TAG, "Listen failed.", error)
                                return@addSnapshotListener
                            }

                            if (documents != null) {
                                val items = documents.mapNotNull { document ->
                                    document.toObject(Message::class.java)
                                }
                                directMessages.value = items
                                messagesLoaded(false)
                                Log.d("MesajGeldi", "Messages: $items")
                            } else {
                                Log.d("MesajGelmedi", "No documents found in messageContext.")
                            }
                        }
                } else {
                    // Handle the case where no conversation document exists
                    Log.d("MesajGelmedi", "No conversation document found.")
                    directMessages.value = emptyList() // Clear any existing messages
                }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error getting documents: ", e)
            }
    }


    fun getAllMessages(userID: String) {
        db.collection("messages")
            .where(
                Filter.or(
                    Filter.equalTo("senderID", userID),
                    Filter.equalTo("receiverID", userID)
                )
            )
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                val sortedItems = result.mapNotNull { document ->
                    document.toObject(Message::class.java)
                }
                messageList.value = sortedItems
                Log.d("Mesajlar", "Messages: ${messageList.value}")
            }
            .addOnFailureListener { exception ->
                Log.w("Mesajlar", "Error getting documents.", exception)
            }
    }

    fun getUserName(userID: String, userName: (String) -> Unit) {
        db.collection("users")
            .document(userID)
            .get()
            .addOnSuccessListener { document ->
                userName(document.getString("name").toString())
                Log.e("UserName", "UserName: ${document.getString("name").toString()}")
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }

    private fun uploadPhoto(uri: Uri, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        val storageRef = fireStorageDB.reference.child("message_photos/${uri.lastPathSegment}")
        val uploadTask = storageRef.putFile(uri)

        uploadTask.addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                onSuccess(downloadUri.toString())
            }.addOnFailureListener { exception ->
                onFailure(exception)
            }
        }.addOnFailureListener { exception ->
            onFailure(exception)
        }
    }

    fun sendPhotoMessage(
        userID: String,
        conversationUserID: String,
        itemID: String,
        photoUri: Uri
    ) {
        uploadPhoto(photoUri, { downloadUrl ->
            // Create a new message with the photo URL
            val photoMessage = Message(
                senderID = userID,
                receiverID = conversationUserID,
                itemID = itemID,
                message = "Photo: $downloadUrl", // Indicate that this message contains a photo
                timestamp = Timestamp.now()
            )
            Log.d("New Photo Message", "Messages: $photoMessage")

            // Save the message in Firestore
            db.collection("messages")
                .whereEqualTo("itemID", itemID)
                .whereIn("senderID", listOf(userID, conversationUserID))
                .whereIn("receiverID", listOf(userID, conversationUserID))
                .get()
                .addOnSuccessListener { result ->
                    if (result.documents.isNotEmpty()) {
                        db.collection("messages")
                            .document(result.documents[0].id)
                            .collection("messageContext")
                            .add(photoMessage)
                    } else {
                        db.collection("messages")
                            .add(photoMessage).addOnSuccessListener {
                                Log.d("First Photo Message", "Messages: $it")
                                db.collection("messages")
                                    .document(it.id)
                                    .collection("messageContext")
                                    .add(photoMessage)
                            }
                    }
                }
        }, { exception ->
            Log.e(TAG, "Error uploading photo: ", exception)
        })
    }


}