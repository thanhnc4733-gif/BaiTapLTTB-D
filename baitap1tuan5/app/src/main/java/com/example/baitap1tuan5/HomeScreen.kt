package com.example.baitap1tuan5

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen(vm: AuthViewModel, onProfile: () -> Unit) {
    val name = vm.user?.displayName ?: "bạn"
    val initials = name.split(" ").mapNotNull { it.firstOrNull()?.uppercase() }
        .takeLast(2).joinToString("").ifEmpty { "?" }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        // Header: avatar bấm vào -> Hồ sơ
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(44.dp).clip(CircleShape)
                    .background(Color(0xFFDDE6FF)).clickable { onProfile() },
                contentAlignment = Alignment.Center
            ) { Text(initials, fontWeight = FontWeight.Bold, color = Color(0xFF2F5BEA)) }
            Spacer(Modifier.width(12.dp))
            Text("Chào $name!", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(16.dp))

        // Thẻ tiến độ
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2F5BEA)),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(20.dp)) {
                Text("Tiến độ hôm nay", color = Color.White.copy(alpha = 0.8f))
                Text("2/4 việc", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                Text("Đã hơn nửa chặng, cố lên!", color = Color.White.copy(alpha = 0.8f))
            }
        }

        Spacer(Modifier.height(20.dp))
        Text("Việc hôm nay", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))

        TaskItem("Tập thể dục 30 phút", "Cuộc sống · 06:30", true)
        TaskItem("Nộp bài tập Lập trình di động", "Học tập · 09:00", true)
        TaskItem("Ôn chương 3 – Cấu trúc dữ liệu", "Học tập · 14:00", false)
        TaskItem("Đi chợ mua đồ cho tuần", "Cuộc sống · 17:30", false)
    }
}

@Composable
fun TaskItem(title: String, sub: String, done: Boolean) {
    Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = done, onCheckedChange = null)
            Spacer(Modifier.width(8.dp))
            Column {
                Text(title, fontWeight = FontWeight.Medium)
                Text(sub, fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}
