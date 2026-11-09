package com.example.baitap1tuan5

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

private data class Tab(val route: String, val label: String, val icon: ImageVector)

private val tabs = listOf(
    Tab("home", "Trang chủ", Icons.Filled.Home),
    Tab("list", "Danh sách", Icons.AutoMirrored.Filled.List),
    Tab("settings", "Cài đặt", Icons.Filled.Settings)
)

@Composable
fun AppScaffold(nav: NavController, current: String, content: @Composable (PaddingValues) -> Unit) {
    Scaffold(
        bottomBar = {
            NavigationBar {
                tabs.forEach { t ->
                    NavigationBarItem(
                        selected = current == t.route,
                        onClick = {
                            nav.navigate(t.route) {
                                popUpTo("home")
                                launchSingleTop = true
                            }
                        },
                        icon = { Icon(t.icon, contentDescription = t.label) },
                        label = { Text(t.label, fontSize = 11.sp) }
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { nav.navigate("add") }) {
                Icon(Icons.Filled.Add, contentDescription = "Thêm việc")
            }
        },
        content = content
    )
}

@Composable
fun Tag(text: String, bg: Color, fg: Color) {
    Surface(shape = RoundedCornerShape(6.dp), color = bg) {
        Text(
            text, fontSize = 11.sp, color = fg,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun CategoryTag(category: String) {
    if (category == "life") Tag("Cuộc sống", Color(0xFFFFE8D2), Color(0xFFB45309))
    else Tag("Học tập", Color(0xFFDDE6FF), Color(0xFF2F5BEA))
}

@Composable
fun AvatarCircle(avatar: ImageBitmap?, initials: String, size: Dp, onClick: (() -> Unit)? = null) {
    var m = Modifier.size(size).clip(CircleShape).background(Color(0xFFDDE6FF))
    if (onClick != null) m = m.clickable { onClick() }
    Box(m, contentAlignment = Alignment.Center) {
        if (avatar != null) {
            Image(
                bitmap = avatar, contentDescription = null,
                modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop
            )
        } else {
            Text(initials, fontWeight = FontWeight.Bold, color = Color(0xFF2F5BEA))
        }
    }
}

@Composable
fun TaskRow(t: Task, onClick: () -> Unit, onToggle: () -> Unit) {
    Card(Modifier.fillMaxWidth().clickable { onClick() }) {
        Row(
            Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(checked = t.done, onCheckedChange = { onToggle() })
            Column(Modifier.weight(1f)) {
                Text(
                    t.title, fontWeight = FontWeight.Medium,
                    textDecoration = if (t.done) TextDecoration.LineThrough else null,
                    color = if (t.done) Color.Gray else Color.Unspecified
                )
                Spacer(Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CategoryTag(t.category)
                    Spacer(Modifier.width(6.dp))
                    Text(friendlyDate(t.date, t.time), fontSize = 12.sp, color = Color.Gray)
                    if (t.priority == "high" && !t.done) {
                        Spacer(Modifier.width(6.dp))
                        Tag("Gấp", Color(0xFFFDE2E2), Color(0xFFB91C1C))
                    }
                }
            }
        }
    }
}

// Hiện vòng xoay khi đang tải, hoặc lỗi + nút Thử lại
@Composable
fun StateBox(loading: Boolean, error: String?, onRetry: () -> Unit) {
    if (loading) {
        Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (error != null) {
        Column(
            Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(error, color = MaterialTheme.colorScheme.error)
            Spacer(Modifier.height(8.dp))
            Button(onClick = onRetry) { Text("Thử lại") }
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

@Composable
fun ChoiceRow(options: List<Pair<String, String>>, selected: String, onSelect: (String) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { (value, label) ->
            FilterChip(
                selected = selected == value,
                onClick = { onSelect(value) },
                label = { Text(label) }
            )
        }
    }
}
