package com.example.baitap1tuan5

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
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
fun HomeScreen(
    vm: AuthViewModel,
    tv: TaskViewModel,
    nav: NavController,
    msgStart: String,
    msgHalf: String,
    msgDone: String
) {
    val context = LocalContext.current
    LaunchedEffect(vm.user?.uid) {
        tv.load()
        vm.loadAvatar()
    }

    val today = todayStr(0)
    val todays = tv.tasks.filter { it.date == today }.sortedBy { it.time }
    val done = todays.count { it.done }
    val progress = if (todays.isEmpty()) 0f else done.toFloat() / todays.size
    val motivation = when {
        todays.isEmpty() || progress < 0.5f -> msgStart
        progress < 1f -> msgHalf
        else -> msgDone
    }

    val name = vm.user?.displayName ?: "bạn"
    val avatar = remember(vm.avatarBase64) { decodeAvatar(vm.avatarBase64) }

    // Quyền LỊCH
    var tick by remember { mutableIntStateOf(0) }
    var calCount by remember { mutableStateOf<Int?>(null) }
    OnResume { tick++ }
    LaunchedEffect(tick) {
        calCount = if (context.isGranted(AppPermission.CALENDAR)) todayEventCount(context) else null
    }
    val request = rememberPermissionRequester { _, _ -> tick++ }

    AppScaffold(nav, "home") { pad ->
        LazyColumn(
            modifier = Modifier.padding(pad).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Spacer(Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AvatarCircle(avatar, initialsOf(name), 48.dp) { nav.navigate("profile") }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(todayTitle(), fontSize = 12.sp, color = Color.Gray)
                        Text("Chào $name!", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2F5BEA)),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text("Tiến độ hôm nay", color = Color.White.copy(alpha = 0.8f))
                            Text(
                                "$done/${todays.size} việc",
                                color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold
                            )
                            Text(motivation, color = Color.White.copy(alpha = 0.8f))
                        }
                        Box(contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(
                                progress = { progress },
                                color = Color.White,
                                trackColor = Color.White.copy(alpha = 0.3f),
                                modifier = Modifier.size(64.dp)
                            )
                            Text("${(progress * 100).toInt()}%", color = Color.White, fontSize = 12.sp)
                        }
                    }
                }
            }

            // Thẻ Lịch (dùng quyền READ_CALENDAR)
            item {
                val c = calCount
                Card(Modifier.fillMaxWidth()) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Event, null, tint = Color(0xFF2F5BEA))
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text("Lịch hôm nay", fontWeight = FontWeight.Bold)
                            Text(
                                if (c != null) "$c sự kiện trong lịch" else "Chưa cấp quyền Lịch",
                                fontSize = 12.sp, color = Color.Gray
                            )
                        }
                        if (c == null) {
                            TextButton(onClick = { request(AppPermission.CALENDAR) }) { Text("Cho phép") }
                        }
                    }
                }
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Card(Modifier.weight(1f)) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Học tập", fontWeight = FontWeight.Bold)
                            Text(
                                "${todays.count { it.category == "study" && !it.done }} việc còn lại",
                                fontSize = 12.sp, color = Color.Gray
                            )
                        }
                    }
                    Card(Modifier.weight(1f)) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Cuộc sống", fontWeight = FontWeight.Bold)
                            Text(
                                "${todays.count { it.category == "life" && !it.done }} việc còn lại",
                                fontSize = 12.sp, color = Color.Gray
                            )
                        }
                    }
                }
            }

            item {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Việc hôm nay", fontSize = 18.sp, fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = { nav.navigate("list") }) { Text("Xem tất cả") }
                }
            }

            item { StateBox(tv.loading, tv.error) { tv.load(true) } }

            items(todays, key = { it.id }) { t ->
                TaskRow(
                    t,
                    onClick = { nav.navigate("detail/${t.id}") },
                    onToggle = {
                        tv.toggle(t)
                        // Quyền THÔNG BÁO: báo khi hoàn thành hết việc hôm nay
                        if (!t.done && done + 1 == todays.size) {
                            Notifier.send(context, "Tuyệt vời!", "Bạn đã hoàn thành hết việc hôm nay")
                        }
                    }
                )
            }
            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}
