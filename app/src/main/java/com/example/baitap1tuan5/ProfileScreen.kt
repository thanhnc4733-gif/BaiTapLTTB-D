package com.example.baitap1tuan5

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(vm: AuthViewModel, tv: TaskViewModel, nav: NavController, onLogout: () -> Unit) {
    val context = LocalContext.current
    val u = vm.user
    val name = u?.displayName ?: "Khách"
    val isGoogle = u?.providerData?.any { it.providerId == "google.com" } == true
    val avatar = remember(vm.avatarBase64) { decodeAvatar(vm.avatarBase64) }

    val done = tv.tasks.count { it.done }
    val pending = tv.tasks.count { !it.done }
    val percent = if (tv.tasks.isEmpty()) 0 else done * 100 / tv.tasks.size

    val gallery = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { vm.uploadAvatar(context, it) }
    }
    val camera = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bmp ->
        bmp?.let { vm.uploadAvatarBitmap(it) }
    }
    // Quyền MÁY ẢNH
    val request = rememberPermissionRequester { _, ok ->
        if (ok) camera.launch(null)
        else Toast.makeText(context, "Cần quyền Máy ảnh để chụp ảnh", Toast.LENGTH_SHORT).show()
    }

    LaunchedEffect(u?.uid) { vm.loadAvatar() }
    LaunchedEffect(vm.message) {
        vm.message?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            vm.clearMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hồ sơ") },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                }
            )
        }
    ) { pad ->
        Column(
            Modifier.fillMaxSize().padding(pad).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(8.dp))
            AvatarCircle(avatar, initialsOf(name), 88.dp)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { request(AppPermission.CAMERA) }) { Text("Chụp ảnh") }
                OutlinedButton(onClick = { gallery.launch("image/*") }) { Text("Chọn từ thư viện") }
            }

            Spacer(Modifier.height(12.dp))
            Text(name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(u?.email ?: "Tài khoản ẩn danh", color = Color.Gray)
            if (isGoogle) {
                Text("✓ Đã liên kết tài khoản Google", color = Color(0xFF2E7D32), fontSize = 12.sp)
            }

            Spacer(Modifier.height(24.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatCard("$done", "Việc đã xong", Modifier.weight(1f))
                StatCard("$pending", "Đang chờ", Modifier.weight(1f))
                StatCard("$percent%", "Hoàn thành", Modifier.weight(1f))
            }

            Spacer(Modifier.weight(1f))
            Button(
                onClick = { vm.signOut(); onLogout() },
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) { Text("Đăng xuất") }
        }
    }
}
