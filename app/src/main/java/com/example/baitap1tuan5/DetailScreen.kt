package com.example.baitap1tuan5

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun DetailScreen(tv: TaskViewModel, id: Int, nav: NavController) {
    val t = tv.tasks.firstOrNull { it.id == id }
    LaunchedEffect(id) { tv.refresh(id) }   // GET /tasks/{id}

    if (t == null) {
        Column(
            Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Không tìm thấy công việc")
            Button(onClick = { nav.popBackStack() }) { Text("Quay lại") }
        }
    } else {
        DetailContent(t, tv, nav)
    }
}

@Composable
private fun DetailContent(t: Task, tv: TaskViewModel, nav: NavController) {
    var editing by remember { mutableStateOf(false) }
    var title by remember(editing) { mutableStateOf(t.title) }
    var note by remember(editing) { mutableStateOf(t.note) }
    var newSub by remember { mutableStateOf("") }
    var confirmDelete by remember { mutableStateOf(false) }

    val subDone = t.subtasks.count { it.done }
    val subProgress = if (t.subtasks.isEmpty()) 0f else subDone.toFloat() / t.subtasks.size

    Scaffold { pad ->
        Column(
            Modifier.padding(pad).padding(horizontal = 16.dp).verticalScroll(rememberScrollState())
        ) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = { nav.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    Spacer(Modifier.width(4.dp))
                    Text("Quay lại")
                }
                Spacer(Modifier.weight(1f))
                TextButton(onClick = {
                    if (editing) tv.update(t.copy(title = title.ifBlank { t.title }, note = note))
                    editing = !editing
                }) { Text(if (editing) "Lưu" else "Sửa") }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                CategoryTag(t.category)
                Tag(priorityLabel(t.priority), Color(0xFFEDEDED), Color.DarkGray)
            }
            Spacer(Modifier.height(8.dp))

            if (editing) {
                OutlinedTextField(
                    value = title, onValueChange = { title = it },
                    label = { Text("Tên công việc") }, modifier = Modifier.fillMaxWidth()
                )
            } else {
                Text(
                    t.title, fontSize = 24.sp, fontWeight = FontWeight.Bold,
                    textDecoration = if (t.done) TextDecoration.LineThrough else null
                )
            }

            Spacer(Modifier.height(12.dp))
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row {
                        Text("Hạn chót", Modifier.weight(1f), color = Color.Gray)
                        Text(friendlyDate(t.date, t.time), fontWeight = FontWeight.Medium)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Nhắc nhở", Modifier.weight(1f), color = Color.Gray)
                        Text(if (t.remind) "Trước 15 phút" else "Tắt", fontWeight = FontWeight.Medium)
                        Spacer(Modifier.width(8.dp))
                        Switch(checked = t.remind, onCheckedChange = { tv.update(t.copy(remind = it)) })
                    }
                    if (t.place.isNotBlank()) {
                        Row {
                            Text("Vị trí", Modifier.weight(1f), color = Color.Gray)
                            Text(t.place, fontWeight = FontWeight.Medium)
                        }
                    }
                    if (t.assignee.isNotBlank()) {
                        Row {
                            Text("Giao cho", Modifier.weight(1f), color = Color.Gray)
                            Text(t.assignee, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
            Text("Ghi chú", fontSize = 13.sp, color = Color.Gray)
            if (editing) {
                OutlinedTextField(
                    value = note, onValueChange = { note = it }, modifier = Modifier.fillMaxWidth()
                )
            } else {
                Card(Modifier.fillMaxWidth()) {
                    Text(
                        t.note.ifBlank { "Chưa có ghi chú" },
                        modifier = Modifier.padding(12.dp),
                        color = if (t.note.isBlank()) Color.Gray else Color.Unspecified
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
            Row {
                Text("Việc nhỏ", Modifier.weight(1f), fontSize = 13.sp, color = Color.Gray)
                Text("$subDone/${t.subtasks.size}", fontSize = 13.sp, color = Color.Gray)
            }
            LinearProgressIndicator(progress = { subProgress }, modifier = Modifier.fillMaxWidth())

            t.subtasks.forEachIndexed { i, s ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = s.done, onCheckedChange = { tv.toggleSub(t, i) })
                    Text(
                        s.title, Modifier.weight(1f),
                        textDecoration = if (s.done) TextDecoration.LineThrough else null
                    )
                    IconButton(onClick = { tv.deleteSub(t, i) }) {
                        Icon(Icons.Filled.Delete, contentDescription = "Xóa việc nhỏ", tint = Color(0xFFB91C1C))
                    }
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = newSub, onValueChange = { newSub = it },
                    placeholder = { Text("Thêm việc nhỏ") }, singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = {
                    if (newSub.isNotBlank()) {
                        tv.addSub(t, newSub.trim())
                        newSub = ""
                    }
                }) { Text("Thêm") }
            }

            Spacer(Modifier.height(24.dp))
            Button(
                onClick = {
                    tv.toggle(t)
                    if (!t.done) nav.popBackStack()
                },
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) { Text(if (t.done) "Đánh dấu chưa xong" else "Đánh dấu hoàn thành") }
            TextButton(
                onClick = { confirmDelete = true },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) { Text("Xóa công việc", color = Color(0xFFB91C1C)) }
            Spacer(Modifier.height(24.dp))
        }
    }

    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text("Xóa công việc?") },
            text = { Text("Hành động này không thể hoàn tác.") },
            confirmButton = {
                TextButton(onClick = {
                    confirmDelete = false
                    tv.delete(t.id)       // DELETE /tasks/{id}
                    nav.popBackStack()
                }) { Text("Xóa", color = Color(0xFFB91C1C)) }
            },
            dismissButton = { TextButton(onClick = { confirmDelete = false }) { Text("Hủy") } }
        )
    }
}
