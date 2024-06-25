package com.android.burakgunduz.bitirmeprojesi.screens.landingPage.components

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.android.burakgunduz.bitirmeprojesi.R
import com.android.burakgunduz.bitirmeprojesi.ui.theme.colors.googleColorChanger
import com.android.burakgunduz.bitirmeprojesi.ui.theme.fonts.archivoFonts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun GoogleAuthButton(navController: NavController, isDarkModeOn: Boolean, textForButton: String) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val context = LocalContext.current
    val token = stringResource(R.string.default_web_client_id)
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
    ) {
        val task =
            try {
                val account = GoogleSignIn.getSignedInAccountFromIntent(it.data)
                    .getResult(ApiException::class.java)
                val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
                FirebaseAuth.getInstance().signInWithCredential(credential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("TAG", "signInWithCredential:success")
                            val user = auth.currentUser
                            if (user != null) {
                                db.collection("users").document(user.uid).get()
                                    .addOnSuccessListener { document ->
                                        if (document.data != null) {
                                            Log.d(
                                                "FirestoreSuccess",
                                                "DocumentSnapshot data: ${document.data.toString()}"
                                            )
                                            navController.navigate("feedScreenNav/${user.uid}")
                                        } else {
                                            Log.d("FirestoreError", "No such document DocumentSnapshot data: ${document.data.toString()}")
                                            val userData: MutableMap<String, Any> = HashMap()
                                            userData["email"] = task.result.user?.email!!
                                            userData["password"] = "password"
                                            userData["name"] = "name"
                                            userData["phoneNumber"] = "phoneNumber"
                                            user.let {
                                                db.collection("users")
                                                    .document(user.uid)
                                                    .set(userData)
                                                    .addOnSuccessListener {
                                                        Log.d(
                                                            "FirestoreSuccess",
                                                            "DocumentSnapshot added with ID: ${user.uid}"
                                                        )
                                                        navController.navigate("feedScreenNav/${user.uid}")
                                                    }
                                                    .addOnFailureListener { e ->
                                                        Log.w(
                                                            "FirestoreError",
                                                            "Error adding document",
                                                            e
                                                        )
                                                    }
                                            }
                                        }
                                    }
                            }


                        } else {
                            Log.w("TAG", "signInWithCredential:failure", task.exception)
                        }
                    }
            } catch (e: ApiException) {
                Log.w("TAG", "GoogleSign in Failed", e)
            }
    }

    ElevatedButton(
        onClick = { googleAuthLogin(token, context, launcher) },
        modifier = Modifier
            .size(350.dp, 75.dp)
            .padding(vertical = 10.dp),
        shape = RoundedCornerShape(7.dp),
        colors = googleColorChanger(isDarkModeOn)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_logo_google),
            contentDescription = ""
        )
        Text(
            text = textForButton,
            modifier = Modifier.padding(6.dp),
            style = MaterialTheme.typography.titleMedium,
            fontFamily = archivoFonts,
            fontWeight = FontWeight.ExtraBold,
        )
    }
}

fun googleAuthLogin(
    token: String,
    context: Context,
    launcher: ManagedActivityResultLauncher<Intent, ActivityResult>
) {
    val gso = GoogleSignInOptions
        .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(token)
        .requestEmail()
        .build()
    val googleSignInClient = GoogleSignIn.getClient(context, gso)
    launcher.launch(googleSignInClient.signInIntent)
}