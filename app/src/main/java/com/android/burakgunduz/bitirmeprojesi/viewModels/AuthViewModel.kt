package com.android.burakgunduz.bitirmeprojesi.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.actionCodeSettings
import com.google.firebase.firestore.FirebaseFirestore

data class User(
    val email: String = "",
    val password: String = "",
    val name: String = "",
    val phoneNumber: String = "",
    val role: Int = 1
)

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val actionCodeSettings = actionCodeSettings { // Action code settings
        url = "https://bitirmeproje-ad56d.firebaseapp.com/__/auth/action?mode=action&oobCode=code"
        handleCodeInApp = true
        setAndroidPackageName(
            "com.android.burakgunduz",
            true, /* installIfNotAvailable */
            "12" /* minimumVersion */
        )
    }

    fun getAuth(
        email: String,
        password: String,
        onCompletion: (Boolean, String) -> Unit,
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("LoginSuccess", "signInWithEmail:success")
                    val user = auth.currentUser
                    onCompletion(false, user!!.uid)
                } else {
                    onCompletion(true, "")
                    Log.w("LoginFail", "signInWithEmail:failure", task.exception)

                }
            }
            .addOnFailureListener {
                Log.w("LoginFail", "signInWithEmail:failure", it)
                onCompletion(true, "")
            }
    }
    fun checkIfEmailExists(
        email: String,
        onCompletion: (Boolean) -> Unit
    ) {
        db.collection("users").whereEqualTo("email",email).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.e("EmailCheckSuccess", task.result.toString())
                    if (task.result.isEmpty) {
                        Log.e("EmailCheckSuccess", task.result.toString())
                        // Email already existsz
                        onCompletion(true)
                    } else {
                        Log.e("EmailCheckSuccess", task.result.toString())
                        // Email does not exist
                        onCompletion(false)
                    }
                } else {
                    // Error occurred
                    Log.w("EmailCheckFail", task.exception)
                    onCompletion(false)
                }
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

                }
                else if (!task.isSuccessful) {
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

    fun sendConfirmation(email: String) {
        auth.sendSignInLinkToEmail(email, actionCodeSettings)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("SendConfirmation", "Email sent.")
                }
                else {
                    Log.d("SendConfirmation", "Email not sent.")
                }
            }
    }
}