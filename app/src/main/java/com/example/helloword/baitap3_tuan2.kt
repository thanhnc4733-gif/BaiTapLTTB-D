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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.helloword.ui.theme.HelloWordTheme

private val Navy = Color.Blue
private val PageBackground = Color.LightGray
private val MainText = Color.Black
private val SecondaryText = Color.DarkGray

class Baitap3Tuan2Activity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HelloWordTheme {
                GradeScreen()
            }
        }
    }
}

// Phân loại điểm bằng if/else theo yêu cầu bài tập.
fun classify(score: Double): String {
    if (score < 0 || score > 10) {
        return "Không hợp lệ"
    } else if (score >= 8.0) {
        return "Giỏi"
    } else if (score >= 6.5) {
        return "Khá"
    } else if (score >= 5.0) {
        return "Trung bình"
    } else {
        return "Yếu"
    }
}

// Chọn màu chữ bằng if/else.
private fun gradeColor(grade: String): Color {
    if (grade == "Giỏi") {
        return Color.Black
    } else if (grade == "Khá") {
        return Color.Blue
    } else if (grade == "Trung bình") {
        return Color.DarkGray
    } else if (grade == "Yếu") {
        return Color.Red
    } else {
        return Color.Gray
    }
}

private fun gradeBackground(grade: String): Color {
    if (grade == "Giỏi") {
        return Color.LightGray
    } else if (grade == "Khá") {
        return Color.LightGray
    } else if (grade == "Trung bình") {
        return Color.LightGray
    } else if (grade == "Yếu") {
        return Color.LightGray
    } else {
        return Color.LightGray
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GradeScreen() {
    val score = 8.5
    val grade = classify(score)
    val testScores = listOf(9.0, 7.0, 5.5, 3.0, 11.0)

    Scaffold(
        containerColor = PageBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Xếp loại học lực",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Navy,
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
                .padding(18.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .border(1.dp, Color.Gray, RoundedCornerShape(16.dp))
                    .padding(horizontal = 18.dp, vertical = 22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Điểm trung bình", fontSize = 14.sp, color = SecondaryText)
                Text(
                    text = score.toString(),
                    fontSize = 56.sp,
                    lineHeight = 62.sp,
                    fontWeight = FontWeight.Bold,
                    color = MainText
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    text = grade,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(gradeBackground(grade), RoundedCornerShape(10.dp))
                        .padding(vertical = 11.dp),
                    color = gradeColor(grade),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }

            Text(
                text = "KIỂM TRA THÊM",
                modifier = Modifier.padding(top = 22.dp, bottom = 8.dp),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = SecondaryText
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .border(1.dp, Color.Gray, RoundedCornerShape(16.dp))
                    .padding(horizontal = 16.dp)
            ) {
                testScores.forEachIndexed { index, testScore ->
                    ScoreRow(testScore)
                    if (index < testScores.lastIndex) {
                        Spacer(
                            Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(Color.Gray)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ScoreRow(score: Double) {
    val grade = classify(score)
    val scoreLabel = if (score % 1.0 == 0.0) score.toInt().toString() else score.toString()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 13.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Điểm $scoreLabel", fontSize = 15.sp, color = Color.DarkGray)
        Text(
            text = grade,
            modifier = Modifier
                .background(gradeBackground(grade), RoundedCornerShape(7.dp))
                .padding(horizontal = 10.dp, vertical = 5.dp),
            color = gradeColor(grade),
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}


