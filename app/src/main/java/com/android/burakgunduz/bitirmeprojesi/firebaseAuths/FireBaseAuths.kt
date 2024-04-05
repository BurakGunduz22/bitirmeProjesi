package com.android.burakgunduz.bitirmeprojesi.firebaseAuths

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

fun getAuth(email: String, password: String,auth: FirebaseAuth) {
    val addOnCompleteListener = auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Log.d("LoginSuccess", "signInWithEmail:success")
                val user = auth.currentUser

            } else {
                // If sign in fails, display a message to the user.
                Log.w("LoginFail", "signInWithEmail:failure", task.exception)

            }
        }
}

fun addUserToDatabase(email: String, password: String,name: String,phoneNumber: String,auth: FirebaseAuth,db: FirebaseFirestore) {
    val addOnCompleteListener = auth.createUserWithEmailAndPassword(email, password)
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
                            Log.d("FirestoreSuccess", "DocumentSnapshot added with ID: ${user.uid}")
                        }
                        .addOnFailureListener { e ->
                            Log.w("FirestoreError", "Error adding document", e)
                        }
                }

            } else {
                // If sign in fails, display a message to the user.
                Log.w("RegisterFail", "createUserWithEmail:failure", task.exception)

            }
        }
}