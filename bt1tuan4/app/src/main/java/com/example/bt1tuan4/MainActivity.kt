package com.example.bt1tuan4



import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay


data class OnboardingPage(val title: String, val description: String, val buttonText: String)

class OnboardingRepository {
    val pages = listOf(
        OnboardingPage("Mọi việc, gọn trong một chỗ", "Ghi lại bài tập, nhắc nhở, lịch hẹn. Không còn quên việc nữa.", "Tiếp tục"),
        OnboardingPage("Chia theo Học tập và Cuộc sống", "Mỗi việc có nhóm riêng, nhìn là biết hôm nay cần làm gì.", "Tiếp tục"),
        OnboardingPage("Nhắc đúng lúc", "Bật thông báo để UTH Todo nhắc bạn trước hạn chót.", "Bắt đầu ngay")
    )
}

private val Blue = Color(0xFFFF5722)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme { UthTodoApp() } }
    }
}

@Composable
fun UthTodoApp() {
    val nav = rememberNavController()
    val pages = OnboardingRepository().pages
    NavHost(nav, startDestination = "splash") {
        composable("splash") {
            SplashScreen { nav.navigate("onboard/0") { popUpTo("splash") { inclusive = true } } }
        }
        composable("onboard/{i}") { entry ->
            val i = entry.arguments?.getString("i")?.toInt() ?: 0
            OnboardingScreen(
                index = i, page = pages[i], total = pages.size,
                onSkip = { nav.navigate("home") { popUpTo(0) } },
                onNext = {
                    if (i < pages.lastIndex) nav.navigate("onboard/${i + 1}")
                    else nav.navigate("home") { popUpTo(0) }
                }
            )
        }
        composable("home") {
            Box(Modifier.fillMaxSize(), Alignment.Center) { Text("Màn hình chính UTH Todo") }
        }
    }
}

@Composable
fun SplashScreen(onDone: () -> Unit) {
    LaunchedEffect(Unit) { delay(1500L); onDone() }
    Column(Modifier.fillMaxSize().background(Blue), Arrangement.Center, Alignment.CenterHorizontally) {
        Text("UTH Todo", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
        Text("Việc nhỏ, việc to – gọn gàng mỗi ngày", color = Color.White, fontSize = 12.sp)
    }
}

@Composable
fun OnboardingScreen(index: Int, page: OnboardingPage, total: Int, onSkip: () -> Unit, onNext: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            Text("UTH Todo", color = Blue, fontWeight = FontWeight.Bold)
            TextButton(onClick = onSkip) { Text("Bỏ qua") }
        }
        Spacer(Modifier.weight(1f))
        Box(Modifier.size(200.dp).background(Color(0xFF673AB7), MaterialTheme.shapes.large))
        Spacer(Modifier.height(32.dp))
        Text(page.title, fontSize = 22.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Spacer(Modifier.height(8.dp))
        Text(page.description, textAlign = TextAlign.Center, color = Color.Gray)
        Spacer(Modifier.weight(1f))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            repeat(total) {
                Box(
                    Modifier.size(if (it == index) 20.dp else 8.dp, 8.dp)
                        .background(if (it == index) Blue else Color.LightGray, MaterialTheme.shapes.small)
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = onNext, modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Blue)
        ) { Text(page.buttonText) }
    }
}