package com.example.baitap1tuan5

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

// ---- 5 quyền của ứng dụng ----
enum class AppPermission(
    val title: String,
    val desc: String,
    val perms: List<String>,
    val icon: ImageVector
) {
    NOTIFICATION(
        "Thông báo", "Nhận nhắc nhở khi công việc sắp đến hạn.",
        if (Build.VERSION.SDK_INT >= 33) listOf(Manifest.permission.POST_NOTIFICATIONS) else emptyList(),
        Icons.Filled.Notifications
    ),
    CAMERA(
        "Máy ảnh", "Chụp ảnh để đặt làm ảnh đại diện.",
        listOf(Manifest.permission.CAMERA), Icons.Filled.CameraAlt
    ),
    LOCATION(
        "Vị trí", "Gắn vị trí hiện tại vào công việc.",
        listOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
        Icons.Filled.LocationOn
    ),
    CONTACTS(
        "Danh bạ", "Chọn người được giao việc từ danh bạ.",
        listOf(Manifest.permission.READ_CONTACTS), Icons.Filled.Contacts
    ),
    CALENDAR(
        "Lịch", "Xem số sự kiện trong lịch hôm nay.",
        listOf(Manifest.permission.READ_CALENDAR), Icons.Filled.Event
    )
}

enum class PermState { GRANTED, DENIED, BLOCKED }

fun Context.isGranted(p: AppPermission): Boolean =
    p.perms.isEmpty() || p.perms.any {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }

fun Context.findActivity(): Activity? {
    var c: Context = this
    while (c is ContextWrapper) {
        if (c is Activity) return c
        c = c.baseContext
    }
    return null
}

// GRANTED: đã cấp | DENIED: có thể xin | BLOCKED: đã từ chối vĩnh viễn, phải vào Cài đặt
fun Context.permState(p: AppPermission): PermState {
    if (isGranted(p)) return PermState.GRANTED
    val asked = getSharedPreferences("perm", Context.MODE_PRIVATE).getBoolean(p.name, false)
    val act = findActivity()
    val rationale = act != null &&
        p.perms.any { ActivityCompat.shouldShowRequestPermissionRationale(act, it) }
    return if (asked && !rationale) PermState.BLOCKED else PermState.DENIED
}

fun openAppSettings(context: Context) {
    val i = Intent(
        android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", context.packageName, null)
    )
    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(i)
}

@Composable
fun OnResume(action: () -> Unit) {
    val owner = LocalLifecycleOwner.current
    val cb = rememberUpdatedState(action)
    DisposableEffect(owner) {
        val obs = LifecycleEventObserver { _, e ->
            if (e == Lifecycle.Event.ON_RESUME) cb.value()
        }
        owner.lifecycle.addObserver(obs)
        onDispose { owner.lifecycle.removeObserver(obs) }
    }
}

// Trả về hàm request(permission). Kết quả trả qua onResult(permission, granted).
@Composable
fun rememberPermissionRequester(onResult: (AppPermission, Boolean) -> Unit): (AppPermission) -> Unit {
    val context = LocalContext.current
    val cb = rememberUpdatedState(onResult)
    var pending by remember { mutableStateOf<AppPermission?>(null) }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val p = pending
        if (p != null) {
            context.getSharedPreferences("perm", Context.MODE_PRIVATE)
                .edit().putBoolean(p.name, true).apply()
            cb.value(p, result.values.any { it })
        }
        pending = null
    }

    return { p ->
        if (context.isGranted(p)) {
            cb.value(p, true)
        } else if (context.permState(p) == PermState.BLOCKED) {
            openAppSettings(context)   // bị chặn vĩnh viễn: đưa người dùng vào Cài đặt
        } else {
            pending = p
            launcher.launch(p.perms.toTypedArray())
        }
    }
}

// ---- Màn hình xin quyền lần đầu (giống slide Nâng cao 01) ----
@Composable
fun PermissionsScreen(onFinished: () -> Unit) {
    val context = LocalContext.current
    val all = AppPermission.values().toList()
    var index by remember { mutableIntStateOf(0) }
    val request = rememberPermissionRequester { _, _ -> index++ }

    LaunchedEffect(index) {
        if (index >= all.size) onFinished()
        else if (context.isGranted(all[index])) index++
    }

    if (index < all.size) {
        val p = all[index]
        Box(
            Modifier.fillMaxSize().background(Color(0xFF1E1E1E)).padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    Modifier.padding(24.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("${index + 1}/${all.size}", fontSize = 12.sp, color = Color.Gray)
                    Spacer(Modifier.height(12.dp))
                    Box(
                        Modifier.size(96.dp).clip(CircleShape).background(Color(0xFFE6F7EF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(p.icon, null, tint = Color(0xFFE8703A), modifier = Modifier.size(48.dp))
                    }
                    Spacer(Modifier.height(16.dp))
                    Text(p.title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Spacer(Modifier.height(8.dp))
                    Text(p.desc, textAlign = TextAlign.Center, color = Color.DarkGray)
                    Spacer(Modifier.height(24.dp))
                    Button(
                        onClick = { request(p) },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE8703A))
                    ) { Text("Cho phép") }
                    Spacer(Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = { index++ },
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) { Text("Để sau", color = Color(0xFFE8703A)) }
                }
            }
        }
    }
}
