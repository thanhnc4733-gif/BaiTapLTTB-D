package com.example.baitap1tuan5

import android.os.Bundle
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.LocationOn
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
import com.google.firebase.analytics.FirebaseAnalytics

@Composable
fun AddTaskScreen(tv: TaskViewModel, nav: NavController) {
    val context = LocalContext.current

    var title by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("study") }
    var priority by remember { mutableStateOf("medium") }
    var date by remember { mutableStateOf(todayStr(0)) }
    var time by remember { mutableStateOf("09:00") }
    var remind by remember { mutableStateOf(true) }
    var place by remember { mutableStateOf("") }
    var assignee by remember { mutableStateOf("") }
    var showContacts by remember { mutableStateOf(false) }
    var contacts by remember { mutableStateOf<List<String>>(emptyList()) }

    val timeOk = Regex("^([01]\\d|2[0-3]):[0-5]\\d$").matches(time)

    // Quyền VỊ TRÍ và DANH BẠ
    val request = rememberPermissionRequester { p, ok ->
        if (!ok) {
            Toast.makeText(context, "Bạn chưa cấp quyền ${p.title}", Toast.LENGTH_SHORT).show()
        } else when (p) {
            AppPermission.LOCATION -> {
                val s = currentPlace(context)
                if (s != null) place = s
                else Toast.makeText(
                    context, "Chưa lấy được vị trí. Hãy bật GPS hoặc đặt vị trí giả lập.",
                    Toast.LENGTH_LONG
                ).show()
            }
            AppPermission.CONTACTS -> {
                contacts = loadContacts(context)
                showContacts = true
            }
            else -> {}
        }
    }

    Scaffold { pad ->
        Column(
            Modifier.padding(pad).padding(horizontal = 16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { nav.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                }
                Text("Thêm công việc", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }

            OutlinedTextField(
                value = title, onValueChange = { title = it },
                label = { Text("Tên công việc") }, singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = note, onValueChange = { note = it },
                label = { Text("Ghi chú") }, modifier = Modifier.fillMaxWidth()
            )

            Text("Danh mục", fontSize = 13.sp, color = Color.Gray)
            ChoiceRow(listOf("study" to "Học tập", "life" to "Cuộc sống"), category) { category = it }

            Text("Mức ưu tiên", fontSize = 13.sp, color = Color.Gray)
            ChoiceRow(listOf("high" to "Cao", "medium" to "Vừa", "low" to "Thấp"), priority) { priority = it }

            Text("Ngày", fontSize = 13.sp, color = Color.Gray)
            ChoiceRow(
                listOf(todayStr(0) to "Hôm nay", todayStr(1) to "Ngày mai", todayStr(2) to "Ngày kia"),
                date
            ) { date = it }
            OutlinedTextField(
                value = time, onValueChange = { time = it },
                label = { Text("Giờ (HH:mm)") }, singleLine = true, isError = !timeOk,
                modifier = Modifier.fillMaxWidth()
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Nhắc nhở trước 15 phút", Modifier.weight(1f))
                Switch(checked = remind, onCheckedChange = { remind = it })
            }

            OutlinedButton(
                onClick = { request(AppPermission.LOCATION) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.LocationOn, null)
                Spacer(Modifier.width(8.dp))
                Text(if (place.isBlank()) "Gắn vị trí hiện tại" else "Vị trí: $place")
            }
            OutlinedButton(
                onClick = { request(AppPermission.CONTACTS) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.Contacts, null)
                Spacer(Modifier.width(8.dp))
                Text(if (assignee.isBlank()) "Giao cho (từ danh bạ)" else "Giao cho: $assignee")
            }

            Button(
                enabled = title.isNotBlank() && timeOk,
                onClick = {
                    tv.add(
                        Task(
                            title = title.trim(), note = note.trim(), category = category,
                            date = date, time = time, priority = priority, remind = remind,
                            place = place, assignee = assignee
                        )
                    )
                    FirebaseAnalytics.getInstance(context).logEvent("add_task", Bundle().apply {
                        putString("category", category)
                    })
                    if (remind) Notifier.send(context, "Đã đặt nhắc nhở", "${title.trim()} lúc $time")
                    nav.popBackStack()
                },
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) { Text("Thêm công việc") }
            Spacer(Modifier.height(24.dp))
        }
    }

    if (showContacts) {
        AlertDialog(
            onDismissRequest = { showContacts = false },
            title = { Text("Chọn người được giao") },
            text = {
                if (contacts.isEmpty()) {
                    Text("Danh bạ trống. Hãy thêm liên hệ trong ứng dụng Contacts.")
                } else {
                    LazyColumn(Modifier.heightIn(max = 300.dp)) {
                        items(contacts) { name ->
                            TextButton(
                                onClick = { assignee = name; showContacts = false },
                                modifier = Modifier.fillMaxWidth()
                            ) { Text(name, Modifier.fillMaxWidth()) }
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showContacts = false }) { Text("Đóng") } }
        )
    }
}
