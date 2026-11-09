package com.example.baitap1tuan5

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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

@Composable
fun SettingsScreen(tv: TaskViewModel, nav: NavController) {
    val context = LocalContext.current
    var tick by remember { mutableIntStateOf(0) }
    OnResume { tick++ }
    val request = rememberPermissionRequester { p, ok ->
        tick++
        if (ok && p == AppPermission.NOTIFICATION) {
            Notifier.send(context, "UTH Todo", "Đây là thông báo thử nghiệm")
        }
    }

    AppScaffold(nav, "settings") { pad ->
        Column(
            Modifier.padding(pad).padding(horizontal = 16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Spacer(Modifier.height(12.dp))
            Text("Cài đặt", fontSize = 26.sp, fontWeight = FontWeight.Bold)

            Card(Modifier.fillMaxWidth().clickable { nav.navigate("profile") }) {
                Text("Hồ sơ cá nhân", modifier = Modifier.padding(16.dp), fontWeight = FontWeight.Medium)
            }

            Text("QUYỀN ỨNG DỤNG", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
            AppPermission.values().forEach { p ->
                val state = remember(tick) { context.permState(p) }
                Card(Modifier.fillMaxWidth()) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(p.icon, null, tint = Color(0xFF2F5BEA))
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(p.title, fontWeight = FontWeight.Bold)
                            Text(p.desc, fontSize = 12.sp, color = Color.Gray)
                            Text(
                                when (state) {
                                    PermState.GRANTED -> "Đã cấp"
                                    PermState.DENIED -> "Chưa cấp"
                                    PermState.BLOCKED -> "Bị chặn (cần bật trong Cài đặt hệ thống)"
                                },
                                fontSize = 12.sp,
                                color = if (state == PermState.GRANTED) Color(0xFF2E7D32) else Color(0xFFB45309)
                            )
                        }
                        when (state) {
                            PermState.GRANTED -> {
                                if (p == AppPermission.NOTIFICATION) {
                                    OutlinedButton(onClick = {
                                        Notifier.send(context, "UTH Todo", "Đây là thông báo thử nghiệm")
                                    }) { Text("Gửi thử") }
                                } else {
                                    Text("✓", color = Color(0xFF2E7D32), fontSize = 20.sp)
                                }
                            }
                            PermState.DENIED -> Button(onClick = { request(p) }) { Text("Cấp quyền") }
                            PermState.BLOCKED -> OutlinedButton(onClick = { openAppSettings(context) }) {
                                Text("Mở cài đặt")
                            }
                        }
                    }
                }
            }

            Text("DỮ LIỆU", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
            Text(
                if (ApiConfig.TOKEN.isBlank()) "Nguồn: dữ liệu mẫu offline" else "Nguồn: Mock API",
                fontSize = 13.sp, color = Color.Gray
            )
            OutlinedButton(onClick = { tv.load(true) }, modifier = Modifier.fillMaxWidth()) {
                Text("Tải lại dữ liệu từ API")
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}
