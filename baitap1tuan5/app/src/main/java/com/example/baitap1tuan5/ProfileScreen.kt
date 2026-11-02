package com.example.baitap1tuan5

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.crashlytics.FirebaseCrashlytics

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(vm: AuthViewModel, onBack: () -> Unit, onLogout: () -> Unit) {
    val context = LocalContext.current
    val u = vm.user
    val name = u?.displayName ?: "Khách"
    val isGoogle = u?.providerData?.any { it.providerId == "google.com" } == true
    val avatar = remember(vm.avatarBase64) { decodeAvatar(vm.avatarBase64) }

    val picker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { vm.uploadAvatar(context, it) }
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
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))
            Box(
                Modifier.size(88.dp).clip(CircleShape).background(Color(0xFFDDE6FF))
                    .clickable { picker.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (avatar != null) {
                    Image(
                        bitmap = avatar, contentDescription = null,
                        modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        name.take(2).uppercase(), fontSize = 26.sp,
                        fontWeight = FontWeight.Bold, color = Color(0xFF2F5BEA)
                    )
                }
            }
            Text("Bấm vào ảnh để đổi", fontSize = 11.sp, color = Color.Gray)

            Spacer(Modifier.height(12.dp))
            Text(name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(u?.email ?: "Tài khoản ẩn danh", color = Color.Gray)
            if (isGoogle) {
                Text("✓ Đã liên kết tài khoản Google", color = Color(0xFF2E7D32), fontSize = 12.sp)
            }

            Spacer(Modifier.height(24.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatCard("2", "Việc đã xong", Modifier.weight(1f))
                StatCard("4", "Đang chờ", Modifier.weight(1f))
                StatCard("33%", "Hoàn thành", Modifier.weight(1f))
            }

            Spacer(Modifier.weight(1f))

            OutlinedButton(
                onClick = {
                    FirebaseCrashlytics.getInstance().log("Nguoi dung bam nut test crash")
                    throw RuntimeException("Test Crashlytics")
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Test Crash (Crashlytics)") }

            Spacer(Modifier.height(8.dp))
            Button(
                onClick = { vm.signOut(); onLogout() },
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) { Text("Đăng xuất") }
        }
    }
}

@Composable
fun StatCard(value: String, label: String, modifier: Modifier = Modifier) {
    Card(modifier) {
        Column(Modifier.padding(12.dp)) {
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(label, fontSize = 11.sp, color = Color.Gray)
        }
    }
}