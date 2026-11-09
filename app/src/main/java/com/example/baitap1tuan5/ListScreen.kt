package com.example.baitap1tuan5

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun ListScreen(tv: TaskViewModel, nav: NavController) {
    LaunchedEffect(Unit) { tv.load() }

    var query by remember { mutableStateOf("") }
    var filter by remember { mutableStateOf("all") }

    val shown = tv.tasks.filter {
        (filter == "all" || it.category == filter) &&
            it.title.contains(query, ignoreCase = true)
    }
    val todo = shown.filter { !it.done }.sortedWith(compareBy<Task>({ it.date }, { it.time }))
    val finished = shown.filter { it.done }

    AppScaffold(nav, "list") { pad ->
        LazyColumn(
            modifier = Modifier.padding(pad).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Spacer(Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Danh sách", fontSize = 26.sp, fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    Text("${tv.tasks.count { !it.done }} việc chưa xong", fontSize = 12.sp, color = Color.Gray)
                    IconButton(onClick = { tv.load(true) }) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Tải lại từ API")
                    }
                }
            }
            item {
                OutlinedTextField(
                    value = query, onValueChange = { query = it },
                    placeholder = { Text("Tìm công việc") },
                    leadingIcon = { Icon(Icons.Filled.Search, null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                ChoiceRow(
                    listOf("all" to "Tất cả", "study" to "Học tập", "life" to "Cuộc sống"),
                    filter
                ) { filter = it }
            }

            item { StateBox(tv.loading, tv.error) { tv.load(true) } }

            item { Text("CẦN LÀM", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray) }
            items(todo, key = { it.id }) { t ->
                TaskRow(t, { nav.navigate("detail/${t.id}") }, { tv.toggle(t) })
            }
            if (todo.isEmpty() && !tv.loading) {
                item { Text("Không có việc cần làm", color = Color.Gray, fontSize = 13.sp) }
            }

            item { Text("ĐÃ XONG", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray) }
            items(finished, key = { it.id }) { t ->
                TaskRow(t, { nav.navigate("detail/${t.id}") }, { tv.toggle(t) })
            }
            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}
