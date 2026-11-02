package com.example.baitaptuan3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ---------- Màu sắc (sửa ở đây là đổi cho cả app) ----------
private val BarBlue = Color(0xFF2196F3)
private val GreenButton = Color(0xFF5CB85C)
private val OrangeButton = Color(0xFFFFA000)
private val CardBlue = Color(0xFFE8F1FB)
private val ScreenBackground = Color(0xFFFDF8FF)

// ---------- Dữ liệu: một phần tử trong danh sách ----------
data class ListItem(val id: Int, val text: String)

private val quotes = listOf(
    "The only way to do great work is to love what you do.",
    "Stay hungry, stay foolish.",
    "Simplicity is the ultimate sophistication.",
    "Talk is cheap. Show me the code.",
    "First, solve the problem. Then, write the code."
)

// Tạo 1.000.000 phần tử ngẫu nhiên.
// "by lazy": chỉ tạo khi dùng lần đầu, sau đó giữ lại để dùng tiếp.
val bigList: List<ListItem> by lazy {
    List(1_000_000) { index -> ListItem(index + 1, quotes.random()) }
}

// ---------- Tên các màn hình ----------
object Routes {
    const val CHOOSE = "choose"
    const val COLUMN = "column"
    const val LAZY = "lazy"
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                AppNavigation()
            }
        }
    }
}

// ---------- Điều hướng giữa các màn hình ----------
@Composable
fun AppNavigation() {
    // Biến nhớ màn hình hiện tại. Giá trị đổi thì giao diện tự đổi theo.
    var current by remember { mutableStateOf(Routes.CHOOSE) }

    // Nút Back của điện thoại: quay về màn hình chọn
    BackHandler(enabled = current != Routes.CHOOSE) {
        current = Routes.CHOOSE
    }

    when (current) {
        Routes.CHOOSE -> ChooseListTypeScreen(
            onColumnClick = { current = Routes.COLUMN },
            onLazyClick = { current = Routes.LAZY }
        )

        Routes.COLUMN -> ColumnListScreen(onBack = { current = Routes.CHOOSE })

        Routes.LAZY -> LazyColumnListScreen(onBack = { current = Routes.CHOOSE })
    }
}

// ---------- Thanh tiêu đề màu xanh dùng chung ----------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(title: String, onBack: (() -> Unit)? = null) {
    TopAppBar(
        title = { Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp) },
        navigationIcon = {
            if (onBack != null) {
                Text(
                    text = "‹",
                    fontSize = 32.sp,
                    color = Color.White,
                    modifier = Modifier
                        .clickable { onBack() }
                        .padding(horizontal = 16.dp)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = BarBlue,
            titleContentColor = Color.White
        )
    )
}

// ---------- Màn hình 1: chọn kiểu danh sách ----------
@Composable
fun ChooseListTypeScreen(onColumnClick: () -> Unit, onLazyClick: () -> Unit) {
    Scaffold(
        containerColor = ScreenBackground,
        topBar = { AppTopBar("Choose List Type") }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = onColumnClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GreenButton)
            ) {
                Text("Column (1,000,000 items)", fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onLazyClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OrangeButton)
            ) {
                Text("LazyColumn (1,000,000 items)", fontSize = 12.sp)
            }
        }
    }
}

// ---------- Màn hình 2: dùng Column thường ----------
// CẢNH BÁO: Column tạo cả 1.000.000 thẻ cùng lúc nên app sẽ đứng hình hoặc bị tắt.
// Muốn thử an toàn, đổi "bigList" thành "bigList.take(10_000)".
@Composable
fun ColumnListScreen(onBack: () -> Unit) {
    Scaffold(
        containerColor = ScreenBackground,
        topBar = { AppTopBar("Column", onBack) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (item in bigList) {
                ListItemCard(item)
            }
        }
    }
}

// ---------- Màn hình 3: dùng LazyColumn ----------
// Chỉ vẽ những thẻ đang hiện trên màn hình nên cuộn mượt dù có 1.000.000 mục.
@Composable
fun LazyColumnListScreen(onBack: () -> Unit) {
    Scaffold(
        containerColor = ScreenBackground,
        topBar = { AppTopBar("LazyColumn", onBack) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(bigList) { item ->
                ListItemCard(item)
            }
        }
    }
}

// ---------- Một thẻ trong danh sách ----------
@Composable
fun ListItemCard(item: ListItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = CardBlue),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${item.id}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.text,
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
            Text(text = "→", fontSize = 20.sp)
        }
    }
}