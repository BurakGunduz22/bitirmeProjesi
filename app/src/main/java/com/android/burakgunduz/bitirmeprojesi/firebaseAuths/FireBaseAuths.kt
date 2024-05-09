package com.android.burakgunduz.bitirmeprojesi.firebaseAuths

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

fun getAuth(
    email: String,
    password: String,
    auth: FirebaseAuth,
    onCompletion: (Boolean) -> Unit
) {
    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Log.d("LoginSuccess", "signInWithEmail:success")
                val user = auth.currentUser
                onCompletion(false)
            } else {
                onCompletion(true)
                Log.w("LoginFail", "signInWithEmail:failure", task.exception)

            }
        }
        .addOnFailureListener() {
            Log.w("LoginFail", "signInWithEmail:failure", it)
            onCompletion(true)
        }
}

fun addUserToDatabase(
    email: String,
    password: String,
    name: String,
    phoneNumber: String,
    auth: FirebaseAuth,
    db: FirebaseFirestore,
    onCompletion: (Boolean) -> Unit
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
                user?.let {
                    db.collection("users")
                        .document(it.uid)
                        .set(userData)
                        .addOnSuccessListener {
                            onCompletion(false)
                            Log.d("FirestoreSuccess", "DocumentSnapshot added with ID: ${user.uid}")
                        }
                        .addOnFailureListener { e ->
                            Log.w("FirestoreError", "Error adding document", e)
                            onCompletion(true)
                        }
                }

            } else {
                onCompletion(false)
                // If sign in fails, display a message to the user.
                Log.w("RegisterFail", "createUserWithEmail:failure", task.exception)

            }
        }
}

fun signOut(auth: FirebaseAuth) {
    auth.signOut()
}