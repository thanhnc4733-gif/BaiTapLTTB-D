package com.example.helloword

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
import java.util.Locale

private data class ScoreStudent(val name: String, val score: Double?)

private val classStudents = listOf(
    ScoreStudent("Nguyễn Văn An", 9.0),
    ScoreStudent("Trần Thị Bình", 7.2),
    ScoreStudent("Lê Minh Chi", null),
    ScoreStudent("Phạm Quốc Dũng", 4.5),
    ScoreStudent("Võ Thanh Em", 5.8)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassScoreScreen() {
    var scoreTotal = 0.0
    var studentsWithScore = 0
    var excellentCount = 0
    var missingCount = 0
    var highestScore: Double? = null

    // Duyệt danh sách bằng for; bỏ qua điểm null khi tính trung bình.
    for (student in classStudents) {
        val score = student.score
        if (score == null) {
            missingCount++
        } else {
            scoreTotal += score
            studentsWithScore++
            if (classify(score) == "Giỏi") {
                excellentCount++
            }
            if (highestScore == null) {
                highestScore = score
            } else if (score > highestScore) {
                highestScore = score
            }
        }
    }

    val average = if (studentsWithScore > 0) scoreTotal / studentsWithScore else 0.0
    val averageText = String.format(Locale.US, "%.2f", average)
    val highestText = highestScore?.let { String.format(Locale.US, "%.1f", it) } ?: "—"

    Scaffold(
        containerColor = Color.LightGray,
        topBar = {
            TopAppBar(
                title = {
                    Text("Bảng điểm lớp", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Blue,
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Blue, RoundedCornerShape(16.dp))
                    .padding(18.dp)
            ) {
                Text("Điểm trung bình lớp", color = Color.LightGray, fontSize = 14.sp)
                Text(
                    averageText,
                    color = Color.White,
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    SummaryItem("Giỏi", excellentCount.toString())
                    SummaryItem("Chưa có điểm", missingCount.toString())
                    SummaryItem("Cao nhất", highestText)
                }
            }

            Text(
                text = "DANH SÁCH · ${classStudents.size} SINH VIÊN",
                modifier = Modifier.padding(top = 18.dp, bottom = 8.dp),
                color = Color.DarkGray,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            for (student in classStudents) {
                StudentScoreCard(student)
                Spacer(Modifier.height(10.dp))
            }
        }
    }
}

@Composable
private fun SummaryItem(label: String, value: String) {
    Column {
        Text(label, color = Color.LightGray, fontSize = 12.sp)
        Text(value, color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun StudentScoreCard(student: ScoreStudent) {
    val score = student.score
    val grade = if (score == null) "Chưa có điểm" else classify(score)
    val gradeColor = if (score == null) Color.Gray else when (grade) {
        "Giỏi" -> Color.Green
        "Khá" -> Color.Blue
        "Trung bình" -> Color.DarkGray
        "Yếu" -> Color.Red
        else -> Color.Gray
    }
    val scoreText = score?.let { String.format(Locale.US, "%.1f", it) } ?: "—"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(12.dp))
            .border(1.dp, Color.Gray, RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 13.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(student.name, color = Color.Black, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            Text(grade, color = gradeColor, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }
        Text(scoreText, color = if (score == null) Color.Gray else Color.Black,
            fontSize = 22.sp, fontWeight = FontWeight.Bold)
    }
}




