package com.example.baitap1tuan5

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import java.io.ByteArrayOutputStream

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    var user by mutableStateOf(auth.currentUser)
        private set
    var message by mutableStateOf<String?>(null)
        private set
    var isLoading by mutableStateOf(false)
        private set
    var avatarBase64 by mutableStateOf<String?>(null)
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
        avatarBase64 = null
    }

    // ---- Avatar lưu trong Realtime Database (Base64) ----
    fun loadAvatar() {
        val uid = auth.currentUser?.uid ?: return
        FirebaseDatabase.getInstance().getReference("users/$uid/avatar")
            .get()
            .addOnSuccessListener { avatarBase64 = it.getValue(String::class.java) }
    }

    fun uploadAvatar(context: Context, uri: Uri) {
        try {
            val input = context.contentResolver.openInputStream(uri)
            val original = BitmapFactory.decodeStream(input)
            input?.close()
            if (original == null) {
                message = "Không đọc được ảnh"
                return
            }
            uploadAvatarBitmap(original)
        } catch (e: Exception) {
            isLoading = false
            message = "Không đọc được ảnh: ${e.localizedMessage}"
        }
    }

    fun uploadAvatarBitmap(original: Bitmap) {
        val uid = auth.currentUser?.uid ?: return
        isLoading = true
        val scale = 256f / maxOf(original.width, original.height)
        val small = Bitmap.createScaledBitmap(
            original,
            (original.width * scale).toInt().coerceAtLeast(1),
            (original.height * scale).toInt().coerceAtLeast(1),
            true
        )
        val out = ByteArrayOutputStream()
        small.compress(Bitmap.CompressFormat.JPEG, 70, out)
        val base64 = Base64.encodeToString(out.toByteArray(), Base64.NO_WRAP)

        FirebaseDatabase.getInstance().getReference("users/$uid/avatar")
            .setValue(base64)
            .addOnCompleteListener { t ->
                isLoading = false
                if (t.isSuccessful) {
                    avatarBase64 = base64
                    message = "Đã cập nhật ảnh đại diện"
                } else {
                    message = "Upload lỗi: ${t.exception?.localizedMessage}"
                }
            }
    }
}
