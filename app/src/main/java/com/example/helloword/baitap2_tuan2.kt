package com.example.helloword

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.helloword.ui.theme.HelloWordTheme

// 1. Class Student: name bắt buộc, email và phone có thể null (String?)
data class Student(
    val name: String,
    val email: String?,
    val phone: String?
)

// 2. List 5 sinh viên, một vài người thiếu email hoặc SĐT
val students = listOf(
    Student("Ngô Trần Phước Thịnh", "an@sv.edu.vn", "0901 234 567"),
    Student("Nguyễn Công Thành", email = "thanh@sv.edu.vn", "0912 888 999"),
    Student("Tả Mạnh Hoàng Thanh", "chi@sv.edu.vn", null),
    Student("Nguyễn Trọng Phú", null, null),
    Student("Tao Tên Là", "em@sv.edu.vn", "0933 111 222")
)

private val NavyColor = Color(0xFF2C4474)
private val RedColor = Color(0xFFC0392B)
private val GrayText = Color(0xFF666666)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HelloWordTheme {
                AssignmentMenu()
            }
        }
    }
}

@Composable
private fun AssignmentMenu() {
    var selectedAssignment by remember { mutableStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp, horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("Bài 1", "Bài 2", "Bài 3", "Bài 4").forEachIndexed { index, title ->
                Button(
                    onClick = { selectedAssignment = index + 1 },
                    modifier = Modifier.weight(1f).padding(horizontal = 3.dp)
                ) {
                    Text(title, fontSize = 12.sp)
                }
            }
        }

        androidx.compose.foundation.layout.Box(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            when (selectedAssignment) {
                1 -> CalculatorScreen(modifier = Modifier.fillMaxSize())
                2 -> StudentListScreen()
                3 -> GradeScreen()
                4 -> ClassScoreScreen()
                else -> Text("Chọn Bài 1, Bài 2 hoặc Bài 3 ở phía trên")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentListScreen() {
    // 5. Dùng for để đếm số sinh viên thiếu email
    var missingEmail = 0
    for (s in students) {
        if (s.email == null) {
            missingEmail++
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFFF3F4F8),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Danh sách sinh viên",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = NavyColor,
                    titleContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {

            // 3. Dùng for để tạo các thẻ sinh viên
            for (s in students) {
                StudentCard(s)
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Thanh tổng kết ở cuối danh sách
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE6ECF5), RoundedCornerShape(10.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = buildAnnotatedString {
                        append("Tổng: ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("${students.size} sinh viên")
                        }
                    },
                    fontSize = 14.sp,
                    color = NavyColor
                )
                Text(
                    text = buildAnnotatedString {
                        append("Thiếu email: ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = RedColor)) {
                            append("$missingEmail")
                        }
                    },
                    fontSize = 14.sp,
                    color = NavyColor
                )
            }
        }
    }
}

// Thẻ của một sinh viên
@Composable
fun StudentCard(s: Student) {
    val shape = RoundedCornerShape(12.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, shape)
            .border(1.dp, Color(0xFFE5E7EB), shape)
            .padding(16.dp)
    ) {
        Text(
            text = s.name,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(4.dp))
        InfoRow("Email", s.email)
        Spacer(modifier = Modifier.height(4.dp))
        InfoRow("SĐT", s.phone)
    }
}

// Một dòng thông tin. 4. Dùng ?: (tương đương ?? của Dart) cho giá trị null
@Composable
fun InfoRow(label: String, value: String?) {
    val missing = value == null
    Row {
        Text(text = "$label: ", fontSize = 13.sp, color = GrayText)
        Text(
            text = value ?: "Chưa cập nhật",
            fontSize = 13.sp,
            color = if (missing) RedColor else GrayText,
            fontStyle = if (missing) FontStyle.Italic else FontStyle.Normal
        )
    }
}




