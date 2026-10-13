package com.example.helloword

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.helloword.ui.theme.HelloWordTheme


// Hàm tính toán tách riêng: nhận 2 chuỗi + phép toán, trả về chuỗi kết quả
fun calculate(aText: String, bText: String, op: String): String {
    val a = aText.trim().toDoubleOrNull()
    val b = bText.trim().toDoubleOrNull()

    if (a == null || b == null) return "Vui lòng nhập số hợp lệ"

    val result = when (op) {
        "+" -> a + b
        "-" -> a - b
        "*" -> a * b
        "/" -> {
            if (b == 0.0) return "Không thể chia cho 0"
            a / b
        }
        else -> return ""
    }

    // 8.0 -> "8", 2.5 -> "2.5"
    return if (result % 1.0 == 0.0) result.toLong().toString() else result.toString()
}

@Composable
fun CalculatorScreen(modifier: Modifier = Modifier) {
    // State: Compose sẽ tự vẽ lại khi các giá trị này thay đổi
    var numberA by remember { mutableStateOf("") }
    var numberB by remember { mutableStateOf("") }
    var selectedOp by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }

    // Mỗi lần bấm nút: lưu phép toán đang chọn và tính kết quả ngay
    val onOpClick: (String) -> Unit = { op ->
        selectedOp = op
        result = calculate(numberA, numberB, op)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(modifier = Modifier.height(100.dp))

        Text(
            text = "Thực hành 03",
            fontSize = 16.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(64.dp))

        OutlinedTextField(
            value = numberA,
            onValueChange = { numberA = it },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OpButton("+", Color.Gray, selectedOp == "+", onOpClick)
            OpButton("-", Color.Yellow, selectedOp == "-", onOpClick)
            OpButton("*", Color.Blue, selectedOp == "*", onOpClick)
            OpButton("/", Color.Black, selectedOp == "/", onOpClick)
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = numberB,
            onValueChange = { numberB = it },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Kết quả: $result", fontSize = 16.sp)
    }
}

@Composable
fun OpButton(
    op: String,
    color: Color,
    isSelected: Boolean,
    onClick: (String) -> Unit
) {
    Button(
        onClick = { onClick(op) },
        modifier = Modifier.size(64.dp),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color),
        // Nút đang chọn có viền đen, giống hình mẫu bên phải
        border = if (isSelected) BorderStroke(3.dp, Color.Black) else null
    ) {
        Text(text = op, fontSize = 20.sp, color = Color.White)
    }
}