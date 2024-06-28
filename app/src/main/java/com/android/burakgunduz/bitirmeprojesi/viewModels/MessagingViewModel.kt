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
    val directMessages: MutableLiveData<List<Message>> = MutableLiveData()
    val messageList: MutableLiveData<List<Message>> by lazy {
        MutableLiveData<List<Message>>().also {
            getAllMessages("userID")
        }
    }

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
                    .add(sendingMessage)
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
            .addOnSuccessListener {
                db.collection("messages")
                    .document(it.documents[0].id)
                    .collection("messageContext")
                    .add(sendingMessage)
            }
    }

    fun getDirectMessages(userID: String, itemID: String, receiverID: String) {
        directMessages.value = emptyList()
        Log.d("MesajlarGeldi", "Messages: $userID, $itemID, $receiverID")
        db.collection("messages")
            .whereEqualTo("itemID", itemID)
            .whereIn("senderID", listOf(userID, receiverID))
            .whereIn("receiverID", listOf(userID, receiverID))
            .get()
            .addOnSuccessListener { result ->
                if (result.documents.isNotEmpty()) {
                    Log.e("MesajGeldi", "Messages: ${result.documents[0].id}")
                    db.collection("messages").document(result.documents[0].id)
                        .collection("messageContext")
                        .get()
                        .addOnSuccessListener { documents ->
                            val items = documents.mapNotNull { document ->
                                document.toObject(Message::class.java)
                            }
                            directMessages.value = items
                            Log.d("MesajGeldi", "Messages: $items")
                        }
                } else {
                    Log.d("MesajGelmedi", "No documents found.")
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
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