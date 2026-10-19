package com.example.baitaptuan3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ---------- Màu dùng chung ----------
private val TitleBlue = Color.Blue
private val ItemBlue = Color.Blue.copy(alpha = 0.2f)
private val ItemRed = Color.Red.copy(alpha = 0.3f)
private val BoxLight = Color.Blue.copy(alpha = 0.4f)
private val BoxDark = Color.Blue.copy(alpha = 0.8f)
private val PanelGray = Color.LightGray.copy(alpha = 0.3f)

// ---------- Danh sách các màn hình của app ----------
enum class UiScreen {
    WELCOME, LIST, TEXT, IMAGES, TEXTFIELD, PASSWORD, COLUMN, ROW,
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                UiComponentsApp()
            }
        }
    }
}

// ---------- "Bộ điều hướng": nhớ màn hình hiện tại và hiển thị đúng màn hình ----------
@Composable
fun UiComponentsApp() {
    var current by remember { mutableStateOf(UiScreen.WELCOME) }

    // Nút Back của điện thoại: về danh sách, từ danh sách về màn hình chào
    BackHandler(enabled = current != UiScreen.WELCOME) {
        current = if (current == UiScreen.LIST) UiScreen.WELCOME else UiScreen.LIST
    }

    val backToList = { current = UiScreen.LIST }

    when (current) {
        UiScreen.WELCOME -> WelcomeScreen(onReady = { current = UiScreen.LIST })
        UiScreen.LIST -> ComponentListScreen(onOpen = { current = it })
        UiScreen.TEXT -> DetailScaffold("Text Detail", backToList) { TextDetailContent() }
        UiScreen.IMAGES -> DetailScaffold("Images", backToList) { ImagesDetailContent() }
        UiScreen.TEXTFIELD -> DetailScaffold("TextField", backToList) { TextFieldDetailContent() }
        UiScreen.PASSWORD -> DetailScaffold("PasswordField", backToList) { PasswordDetailContent() }
        UiScreen.COLUMN -> DetailScaffold("Column Layout", backToList) { ColumnDetailContent() }
        UiScreen.ROW -> DetailScaffold("Row Layout", backToList) { RowLayoutDetailContent() }
    }
}

// ---------- Màn hình 1: Chào mừng ----------
@Composable
fun WelcomeScreen(onReady: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .safeDrawingPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(Color.Blue),
            contentAlignment = Alignment.Center
        ) {
            Text("Jc", fontSize = 48.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Jetpack Compose", fontSize = 14.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Jetpack Compose is a modern UI toolkit for building native Android " +
                    "applications using a declarative programming approach.",
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onReady,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
        ) {
            Text("I'm ready")
        }
    }
}

// ---------- Màn hình 2: Danh sách thành phần ----------
@Composable
fun ComponentListScreen(onOpen: (UiScreen) -> Unit) {
    Scaffold(containerColor = Color.White) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = "UI Components List",
                color = TitleBlue,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            SectionTitle("Display")
            ListItemCard("Text", "Displays text") { onOpen(UiScreen.TEXT) }
            ListItemCard("Image", "Displays an image") { onOpen(UiScreen.IMAGES) }

            SectionTitle("Input")
            ListItemCard("TextField", "Input field for text") { onOpen(UiScreen.TEXTFIELD) }
            ListItemCard("PasswordField", "Input field for passwords") { onOpen(UiScreen.PASSWORD) }

            SectionTitle("Layout")
            ListItemCard("Column", "Arranges elements vertically") { onOpen(UiScreen.COLUMN) }
            ListItemCard("Row", "Arranges elements horizontally") { onOpen(UiScreen.ROW) }

             }
            }
        }



@Composable
fun SectionTitle(text: String) {
    Text(
        text = text,
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.padding(top = 8.dp, bottom = 6.dp)
    )
}

@Composable
fun ListItemCard(
    title: String,
    subtitle: String,
    color: Color = ItemBlue,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(color)
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        Text(title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        Text(subtitle, fontSize = 13.sp)
    }
}

// ---------- Khung chung cho các màn hình chi tiết: thanh tiêu đề + nút quay lại ----------
@Composable
fun DetailScaffold(
    title: String,
    onBack: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Scaffold(containerColor = Color.White) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 8.dp)
            ) {
                Text(
                    text = "‹",
                    fontSize = 32.sp,
                    color = TitleBlue,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .clickable { onBack() }
                        .padding(horizontal = 12.dp)
                )
                Text(
                    text = title,
                    fontSize = 18.sp,
                    color = TitleBlue,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            content()
        }
    }
}

// ---------- Chi tiết: Text (nhiều kiểu chữ trong cùng một câu) ----------
@Composable
fun TextDetailContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = buildAnnotatedString {
                append("The ")
                withStyle(SpanStyle(textDecoration = TextDecoration.LineThrough)) { append("quick") }
                append(" ")
                withStyle(SpanStyle(fontSize = 34.sp, color = Color.Red)) { append("Brown") }
                append("\nfox ")
                withStyle(SpanStyle(letterSpacing = 6.sp)) { append("jumps") }
                append(" ")
                withStyle(
                    SpanStyle(fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic)
                ) { append("over") }
                append("\n")
                withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) { append("the") }
                append(" ")
                withStyle(
                    SpanStyle(
                        fontFamily = FontFamily.Cursive,
                        fontStyle = FontStyle.Italic,
                        fontSize = 18.sp
                    )
                ) { append("lazy") }
                append(" dog.")
            },
            fontSize = 26.sp,
            lineHeight = 44.sp
        )
    }
}

// ---------- Chi tiết: Image ----------
// Ảnh đang dùng tạm ic_launcher_foreground có sẵn trong project.
// Muốn dùng ảnh của bạn: kéo ảnh vào res/drawable rồi đổi tên R.drawable.xxx bên dưới.
@Composable
fun ImagesDetailContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.uth),
            contentDescription = "Ảnh 1",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.Blue.copy(alpha = 0.1f))
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "https://giaothongvantaitphcm.edu.vn/wp-content/uploads/2025/01/Logo-GTVT.png",
            fontSize = 11.sp,
            textDecoration = TextDecoration.Underline,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        Image(
            painter = painterResource(id = R.drawable.uth),
            contentDescription = "Ảnh 2",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.Blue.copy(alpha = 0.1f))
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text("In app", fontSize = 12.sp, color = Color.Gray)
    }
}

// ---------- Chi tiết: TextField (gõ chữ, dòng đỏ tự cập nhật) ----------
@Composable
fun TextFieldDetailContent() {
    var text by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            placeholder = { Text("Thông tin nhập", fontSize = 13.sp) },
            singleLine = true,
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = if (text.isEmpty()) "Tự động cập nhật dữ liệu theo textfield" else text,
            color = Color.Red,
            fontSize = 12.sp,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

// ---------- Chi tiết: PasswordField ----------
@Composable
fun PasswordDetailContent() {
    var password by remember { mutableStateOf("") }
    var show by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = { Text("Mật khẩu", fontSize = 13.sp) },
            singleLine = true,
            shape = RoundedCornerShape(24.dp),
            visualTransformation = if (show) VisualTransformation.None
            else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = show, onCheckedChange = { show = it })
            Text("Hiện mật khẩu", fontSize = 13.sp)
        }
    }
}

// ---------- Chi tiết: Column (xếp dọc) ----------
@Composable
fun ColumnDetailContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(PanelGray, RoundedCornerShape(10.dp))
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        for (i in 1..4) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .background(if (i == 2) BoxDark else BoxLight, RoundedCornerShape(8.dp))
            )
        }
    }
}

// ---------- Chi tiết: Row (xếp ngang) - 4 hàng, mỗi hàng 3 ô ----------
@Composable
fun RowLayoutDetailContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        for (i in 1..4) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PanelGray, RoundedCornerShape(10.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (j in 1..3) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .background(if (j == 2) BoxDark else BoxLight, RoundedCornerShape(8.dp))
                    )
                }
            }
        }
    }
}

