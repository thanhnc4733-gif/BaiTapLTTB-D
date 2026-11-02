package com.example.bt3tuan4
// GIỮ NGUYÊN dòng "package ..." của project bạn ở trên cùng, rồi dán phần dưới đây ngay sau nó.

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

// Đối tượng dữ liệu đi xuyên suốt luồng: email -> otp -> password
data class ResetData(val email: String, val otp: String, val password: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme { ForgotPasswordApp() } }
    }
}

@Composable
fun ForgotPasswordApp() {
    val nav = rememberNavController()

    // Luồng dữ liệu: mỗi màn hình truyền dữ liệu sang màn kế tiếp qua route:
    // forgot -> verify/{email} -> reset/{email}/{otp} -> confirm/{email}/{otp}/{pass}
    NavHost(nav, startDestination = "forgot") {

        // Màn 1: nhập email
        composable("forgot") {
            var email by remember { mutableStateOf("") }
            FormPage("Forget Password?", "Enter your Email, we will send you a verification code.", false, {}) {
                OutlinedTextField(email, { email = it }, Modifier.fillMaxWidth(),
                    label = { Text("Your Email") }, singleLine = true)
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = { nav.navigate("verify/${Uri.encode(email)}") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = email.contains("@")
                ) { Text("Next") }
            }
        }

        // Màn 2: nhập mã OTP (nhận email từ màn 1)
        composable("verify/{email}") { entry ->
            val email = entry.arguments?.getString("email") ?: ""
            var otp by remember { mutableStateOf("") }
            FormPage("Verify Code", "Enter the code we just sent you on your registered Email", true, { nav.popBackStack() }) {
                OutlinedTextField(otp, { if (it.length <= 5) otp = it }, Modifier.fillMaxWidth(),
                    label = { Text("Code (5 số)") }, singleLine = true)
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = { nav.navigate("reset/${Uri.encode(email)}/${Uri.encode(otp)}") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = otp.length == 5
                ) { Text("Next") }
            }
        }

        // Màn 3: tạo mật khẩu mới (nhận email + otp)
        composable("reset/{email}/{otp}") { entry ->
            val email = entry.arguments?.getString("email") ?: ""
            val otp = entry.arguments?.getString("otp") ?: ""
            var pass by remember { mutableStateOf("") }
            var confirm by remember { mutableStateOf("") }
            FormPage("Create new password", "Your new password must be different from previously used password", true, { nav.popBackStack() }) {
                OutlinedTextField(pass, { pass = it }, Modifier.fillMaxWidth(),
                    label = { Text("Password") }, visualTransformation = PasswordVisualTransformation(), singleLine = true)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(confirm, { confirm = it }, Modifier.fillMaxWidth(),
                    label = { Text("Confirm Password") }, visualTransformation = PasswordVisualTransformation(), singleLine = true)
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = { nav.navigate("confirm/${Uri.encode(email)}/${Uri.encode(otp)}/${Uri.encode(pass)}") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = pass.isNotEmpty() && pass == confirm
                ) { Text("Next") }
            }
        }

        // Màn 4: xác nhận (nhận đủ email + otp + password từ các màn trước)
        composable("confirm/{email}/{otp}/{pass}") { entry ->
            val data = ResetData(
                email = entry.arguments?.getString("email") ?: "",
                otp = entry.arguments?.getString("otp") ?: "",
                password = entry.arguments?.getString("pass") ?: ""
            )
            FormPage("Confirm", "We are here to help you!", true, { nav.popBackStack() }) {
                OutlinedTextField(data.email, {}, Modifier.fillMaxWidth(), readOnly = true, label = { Text("Email") })
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(data.otp, {}, Modifier.fillMaxWidth(), readOnly = true, label = { Text("OTP") })
                Spacer(Modifier.height(8.dp))
                OutlinedTextField("•".repeat(data.password.length), {}, Modifier.fillMaxWidth(),
                    readOnly = true, label = { Text("Password") })
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = { nav.navigate("forgot") { popUpTo("forgot") { inclusive = true } } },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Submit") }
            }
        }
    }
}

@Composable
fun FormPage(
    title: String,
    subtitle: String,
    showBack: Boolean,
    onBack: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(Modifier.fillMaxWidth().height(48.dp)) {
            if (showBack) TextButton(onClick = onBack) { Text("< Back") }
        }
        Spacer(Modifier.height(24.dp))
        Text("UTH SmartTasks", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(24.dp))
        Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
        Text(subtitle, style = MaterialTheme.typography.bodySmall)
        Spacer(Modifier.height(24.dp))
        content()
    }
}