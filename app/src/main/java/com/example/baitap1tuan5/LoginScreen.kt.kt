package com.example.baitap1tuan5

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(vm: AuthViewModel, onSuccess: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(vm.message) {
        vm.message?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            val ok = vm.user != null
            vm.clearMessage()
            if (ok) onSuccess()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Bottom
    ) {
        Box(
            Modifier.size(64.dp).clip(RoundedCornerShape(16.dp)).background(Color(0xFF2F5BEA)),
            contentAlignment = Alignment.Center
        ) { Text("✓", color = Color.White, fontSize = 28.sp) }

        Spacer(Modifier.height(24.dp))
        Text("Chào mừng đến với\nUTH Todo", fontSize = 26.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text(
            "Đăng nhập để lưu công việc và đồng bộ trên mọi thiết bị của bạn.",
            color = Color.Gray
        )

        Spacer(Modifier.height(120.dp))

        OutlinedButton(
            onClick = { scope.launch { googleSignIn(context, vm) } },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            enabled = !vm.isLoading
        ) { Text("Tiếp tục với Google") }

        Spacer(Modifier.height(8.dp))
        Text("hoặc", Modifier.align(Alignment.CenterHorizontally), color = Color.Gray)

        TextButton(
            onClick = { vm.signInAnonymously() },
            modifier = Modifier.align(Alignment.CenterHorizontally),
            enabled = !vm.isLoading
        ) { Text("Dùng thử không cần tài khoản") }

        if (vm.isLoading) {
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(Modifier.fillMaxWidth())
        }

        Spacer(Modifier.height(16.dp))
        Text(
            "Khi tiếp tục, bạn đồng ý với Điều khoản sử dụng và Chính sách quyền riêng tư của UTH Todo.",
            fontSize = 11.sp, color = Color.Gray
        )
    }
}

suspend fun googleSignIn(context: Context, vm: AuthViewModel) {
    val option = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false)
        .setServerClientId(context.getString(R.string.default_web_client_id))
        .build()
    val request = GetCredentialRequest.Builder().addCredentialOption(option).build()

    try {
        val result = CredentialManager.create(context).getCredential(context, request)
        val cred = result.credential
        if (cred is CustomCredential &&
            cred.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            val token = GoogleIdTokenCredential.createFrom(cred.data).idToken
            vm.signInWithGoogleToken(token)
        } else {
            vm.reportError("Đăng nhập lỗi: loại tài khoản không hợp lệ")
        }
    } catch (e: Exception) {
        vm.reportError("Đăng nhập lỗi: ${e.localizedMessage}")
    }
}