package com.android.burakgunduz.bitirmeprojesi.viewModels

import android.content.ContentValues.TAG
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.actionCodeSettings
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.storage


data class User(
    val email: String = "",
    val password: String = "",
    val name: String = "",
    val phoneNumber: String = "",
    val role: Int = 1,
    val userID: String = "",
)

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val storage = Firebase.storage("gs://bitirmeproje-ad56d.appspot.com")
    private val actionCodeSettings = actionCodeSettings { // Action code settings
        url = "https://bitirmeproje-ad56d.firebaseapp.com/__/auth/action?mode=action&oobCode=code"
        handleCodeInApp = true
        setAndroidPackageName(
            "com.android.burakgunduz",
            true, /* installIfNotAvailable */
            "12" /* minimumVersion */
        )
    }
    val sellerProfile = MutableLiveData<MutableState<User>>()
    val sellerImage = MutableLiveData<Uri>()
    val userInfo = MutableLiveData<User?>()
    fun getAuth(
        email: String,
        password: String,
        onCompletion: (Boolean, String, String) -> Unit,
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        // Retrieve the user's role from Firestore
                        db.collection("users").document(user.uid).get()
                            .addOnSuccessListener { document ->
                                if (document.exists()) {
                                    val role = document.getLong("role")
                                    if (role == 1L || role == 3L) {
                                        // Role is valid, proceed with login
                                        Log.d("LoginSuccess", "signInWithEmail:success")
                                        onCompletion(false, user.uid, "You successfully signed in")
                                    } else {
                                        // Role is not valid, sign out and show error
                                        auth.signOut()
                                        Log.w("LoginFail", "Invalid role")
                                        onCompletion(true, "", "You are banned")
                                    }
                                } else {
                                    // Document does not exist, sign out and show error
                                    auth.signOut()
                                    Log.w("LoginFail", "No such document")
                                    onCompletion(true, "", "There is no such user")
                                }
                            }
                            .addOnFailureListener { exception ->
                                // Error retrieving document, sign out and show error
                                auth.signOut()
                                Log.w("LoginFail", "get failed with ", exception)
                                onCompletion(true, "", "")
                            }
                    }
                } else {
                    onCompletion(true, "", "Invalid Info")
                    Log.w("LoginFail", "signInWithEmail:failure", task.exception)
                }
            }
            .addOnFailureListener {
                Log.w("LoginFail", "signInWithEmail:failure", it)
                onCompletion(true, "", "There is no such user")
            }
    }

    fun addUserToDatabase(
        email: String,
        password: String,
        name: String,
        phoneNumber: String,
        onCompletion: (Boolean, String) -> Unit,
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("RegisterSuccess", "createUserWithEmail:success")
                    val user = auth.currentUser
                    val userData: MutableMap<String, Any> = HashMap()
                    userData["email"] = email
                    userData["password"] = password
                    userData["name"] = name
                    userData["phoneNumber"] = phoneNumber
                    userData["role"] = 1
                    user?.let {
                        db.collection("users")
                            .document(it.uid)
                            .set(userData)
                            .addOnSuccessListener {
                                onCompletion(false, user.uid)
                                Log.d(
                                    "FirestoreSuccess",
                                    "DocumentSnapshot added with ID: ${user.uid}"
                                )
                            }
                            .addOnFailureListener { e ->
                                Log.w("FirestoreError", "Error adding document", e)
                                onCompletion(true, user.uid)
                            }
                    }

                } else if (!task.isSuccessful) {
                    onCompletion(true, "")
                    // If sign in fails, display a message to the user.
                    Log.w("RegisterFail", "createUserWithEmail:failure", task.exception)

                }
            }
    }

    fun signOut(auth: FirebaseAuth) {
        auth.signOut()
    }

    fun resetPassword(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("ResetPassword", "Email sent.")
                }
            }
    }

    fun updateUserDetails(
        phoneNumber: String,
        name: String,
        onCompletion: (Boolean, String) -> Unit
    ) {
        val user = auth.currentUser
        if (user != null) {
            val userData = mapOf(
                "phoneNumber" to phoneNumber,
                "name" to name
            )
            db.collection("users").document(user.uid)
                .update(userData)
                .addOnSuccessListener {
                    onCompletion(true, "Details updated successfully")
                }
                .addOnFailureListener { e ->
                    Log.w("FirestoreError", "Error updating document", e)
                    onCompletion(false, "Error updating details")
                }
        } else {
            onCompletion(false, "User not logged in")
        }
    }

    fun getUserProfile(userID: String) {
        val itemStorageRef = storage.reference.child("userProfileImages")
        db.collection("users")
            .document(userID)
            .get()
            .addOnSuccessListener { document ->
                val userProfile = document.toObject(User::class.java)?.copy(userID = document.id)
                Log.d(TAG, "DocumentSnapshot data: $userProfile")
                sellerProfile.value = mutableStateOf(userProfile!!)
                sellerImage.value = "".toUri()
                itemStorageRef.child("$userID/1.png").downloadUrl
                    .addOnSuccessListener {
                        sellerImage.value = it
                        Log.d("SellerImage", "SellerImage: $it")
                        Log.d("SellerProfile", "SellerProfile: ${sellerProfile.value}")
                    }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
                Log.d(TAG, "DocumentSnapshot data: ${sellerProfile.value}")
            }
    }

    fun uploadProfilePhoto(
        userID: String,
        uri: Uri,
        onCompletion: (Boolean, String) -> Unit
    ) {
        val storageRef = storage.reference.child("userProfileImages/$userID/1.png")
        storageRef.putFile(uri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    val userData = mapOf("photoUrl" to downloadUrl.toString())
                    db.collection("users").document(userID)
                        .update(userData)
                        .addOnSuccessListener {
                            onCompletion(true, "Photo updated successfully")
                        }
                        .addOnFailureListener { e ->
                            Log.w("FirestoreError", "Error updating photo URL", e)
                            onCompletion(false, "Error updating photo URL")
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.w("StorageError", "Error uploading photo", e)
                onCompletion(false, "Error uploading photo")


            }
    }
}