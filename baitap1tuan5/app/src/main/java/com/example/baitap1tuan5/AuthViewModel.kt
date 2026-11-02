
package com.example.baitap1tuan5

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    var user by mutableStateOf(auth.currentUser)
        private set
    var message by mutableStateOf<String?>(null)
        private set
    var isLoading by mutableStateOf(false)
        private set

    fun signInWithGoogleToken(idToken: String) {
        isLoading = true
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            isLoading = false
            if (task.isSuccessful) {
                user = auth.currentUser
                message = "Đăng nhập thành công!"
            } else {
                message = "Đăng nhập lỗi: ${task.exception?.localizedMessage}"
            }
        }
    }

    fun signInAnonymously() {
        isLoading = true
        auth.signInAnonymously().addOnCompleteListener { task ->
            isLoading = false
            if (task.isSuccessful) {
                user = auth.currentUser
                message = "Đăng nhập thành công!"
            } else {
                message = "Đăng nhập lỗi: ${task.exception?.localizedMessage}"
            }
        }
    }

    fun reportError(msg: String) { message = msg }
    fun clearMessage() { message = null }

    fun signOut() {
        auth.signOut()
        user = null
    }
}