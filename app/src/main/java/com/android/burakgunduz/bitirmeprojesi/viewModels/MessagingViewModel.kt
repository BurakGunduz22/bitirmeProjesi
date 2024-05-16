package com.android.burakgunduz.bitirmeprojesi.viewModels

import android.content.ContentValues.TAG
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
    val timestamp: Timestamp = Timestamp.now()
)

data class ConversationID(
    val senderID: String = "",
    val itemID: String = "",
    val timestamp: Timestamp = Timestamp.now()
)

class MessageViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val fireStorageDB = Firebase.storage("gs://bitirmeproje-ad56d.appspot.com")
    private val storageRef = fireStorageDB.reference
    val directMessages: MutableLiveData<List<Message>> by lazy {
        MutableLiveData<List<Message>>().also {
            getDirectMessages("senderID", "itemID", "receiverID")
        }
    }
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
            .where(
                Filter.or(
                    Filter.and(
                        Filter.equalTo("senderID", userID),
                        Filter.equalTo("receiverID", conversationUserID)
                    ),
                    Filter.and(
                        Filter.equalTo("senderID", conversationUserID),
                        Filter.equalTo("receiverID", userID)
                    )
                )
            )
            .get()
            .addOnSuccessListener {
                db.collection("messages")
                    .document(it.documents[0].id)
                    .collection("messageContext")
                    .add(sendingMessage)
            }
    }

    fun getDirectMessages(userID: String, itemID: String, receiverID: String) {
        Log.d("MesajlarGeldi", "Messages: $userID, $itemID, $receiverID")
        db.collection("messages")
            .where(
                Filter.or(
                    Filter.and(
                        Filter.equalTo("itemID", itemID),
                        Filter.equalTo("receiverID", userID),
                        Filter.equalTo("senderID", receiverID)
                    ),
                    Filter.and(
                        Filter.equalTo("senderID", userID),
                        Filter.equalTo("itemID", itemID),
                        Filter.equalTo("receiverID", receiverID)
                    )
                )
            )
            .get()
            .addOnSuccessListener { result ->
                if (result.documents.isNotEmpty()) {
                    db.collection("messages").document(result.documents[0].id)
                        .collection("messageContext")
                        .get()
                        .addOnSuccessListener { result ->

                            val items = result.mapNotNull { document ->
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
}