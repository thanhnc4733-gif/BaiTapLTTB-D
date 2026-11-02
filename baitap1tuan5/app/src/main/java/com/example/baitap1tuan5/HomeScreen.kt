package com.example.baitap1tuan5

import android.os.Bundle
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.analytics.FirebaseAnalytics

@Composable
fun HomeScreen(
    vm: AuthViewModel,
    tv: TaskViewModel,
    motivation: String,
    onProfile: () -> Unit
) {
    val context = LocalContext.current
    LaunchedEffect(vm.user?.uid) {
        tv.start()
        vm.loadAvatar()
    }

    var showDialog by remember { mutableStateOf(false) }
    val tasks = tv.tasks
    val done = tasks.count { it.done }
    val progress = if (tasks.isEmpty()) 0f else done.toFloat() / tasks.size

    val name = vm.user?.displayName ?: "bạn"
    val initials = name.split(" ").mapNotNull { it.firstOrNull()?.uppercase() }
        .takeLast(2).joinToString("").ifEmpty { "?" }
    val avatar = remember(vm.avatarBase64) { decodeAvatar(vm.avatarBase64) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Thêm việc")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Spacer(Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier.size(48.dp).clip(CircleShape)
                            .background(Color(0xFFDDE6FF)).clickable { onProfile() },
                        contentAlignment = Alignment.Center
                    ) {
                        if (avatar != null) {
                            Image(
                                bitmap = avatar, contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text(initials, fontWeight = FontWeight.Bold, color = Color(0xFF2F5BEA))
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    Text("Chào $name!", fontSize = 20.sp, fontWeight = FontWeight.Bold)
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
                                "$done/${tasks.size} việc",
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

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CategoryCard(
                        "Học tập",
                        tasks.count { it.category == "Học tập" && !it.done },
                        Modifier.weight(1f)
                    )
                    CategoryCard(
                        "Cuộc sống",
                        tasks.count { it.category == "Cuộc sống" && !it.done },
                        Modifier.weight(1f)
                    )
                }
            }

            item { Text("Việc hôm nay", fontSize = 18.sp, fontWeight = FontWeight.Bold) }

            items(tasks, key = { it.id }) { t ->
                TaskRow(t, onToggle = { tv.toggle(t) }, onDelete = { tv.delete(t) })
            }
            item { Spacer(Modifier.height(80.dp)) }
        }
    }

    if (showDialog) {
        AddTaskDialog(
            onDismiss = { showDialog = false },
            onAdd = { title, cat, time ->
                tv.add(title, cat, time)
                FirebaseAnalytics.getInstance(context).logEvent("add_task", Bundle().apply {
                    putString("category", cat)
                })
                showDialog = false
            }
        )
    }
}

@Composable
fun CategoryCard(title: String, remaining: Int, modifier: Modifier = Modifier) {
    Card(modifier) {
        Column(Modifier.padding(16.dp)) {
            Text(title, fontWeight = FontWeight.Bold)
            Text("$remaining việc còn lại", fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
fun TaskRow(t: TodoTask, onToggle: () -> Unit, onDelete: () -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Row(
            Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(checked = t.done, onCheckedChange = { onToggle() })
            Column(Modifier.weight(1f)) {
                Text(
                    t.title, fontWeight = FontWeight.Medium,
                    textDecoration = if (t.done) TextDecoration.LineThrough else null
                )
                Text("${t.category} · ${t.time}", fontSize = 12.sp, color = Color.Gray)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Xoá")
            }
        }
    }
}

@Composable
fun AddTaskDialog(onDismiss: () -> Unit, onAdd: (String, String, String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("09:00") }
    var category by remember { mutableStateOf("Học tập") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Thêm công việc") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title, onValueChange = { title = it },
                    label = { Text("Tên công việc") }, singleLine = true
                )
                OutlinedTextField(
                    value = time, onValueChange = { time = it },
                    label = { Text("Giờ (HH:mm)") }, singleLine = true
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = category == "Học tập",
                        onClick = { category = "Học tập" },
                        label = { Text("Học tập") }
                    )
                    FilterChip(
                        selected = category == "Cuộc sống",
                        onClick = { category = "Cuộc sống" },
                        label = { Text("Cuộc sống") }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { if (title.isNotBlank()) onAdd(title.trim(), category, time) }) {
                Text("Thêm")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Huỷ") } }
    )
}