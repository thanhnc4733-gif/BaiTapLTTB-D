package com.example.baitap2tuan4

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

// ================= 4 ĐẶC TRƯNG OOP =================

// 1. TRỪU TƯỢNG (Abstraction): lớp trừu tượng, chưa biết mô tả cụ thể
abstract class LibraryItem(val id: Int, val title: String) {
    abstract fun describe(): String
}

// 2. KẾ THỪA (Inheritance): Book và Magazine kế thừa LibraryItem
// 4. ĐA HÌNH (Polymorphism): mỗi lớp con override describe() theo cách riêng
class Book(id: Int, title: String, val author: String) : LibraryItem(id, title) {
    override fun describe() = "Sách #$id: $title - $author"
}

class Magazine(id: Int, title: String, val issue: Int) : LibraryItem(id, title) {
    override fun describe() = "Tạp chí #$id: $title - số $issue"
}

class Student(val id: Int, var name: String) {
    // 3. ĐÓNG GÓI (Encapsulation): danh sách mượn là private,
    // bên ngoài chỉ đọc qua borrowedBooks và thêm qua borrow()
    private val borrowed = mutableListOf<Book>()
    val borrowedBooks: List<Book> get() = borrowed

    fun borrow(book: Book): Boolean {
        if (borrowed.any { it.id == book.id }) return false
        borrowed.add(book)
        return true
    }
}

class Library {
    val books = mutableStateListOf(
        Book(1, "Sách 01", "Tác giả A"),
        Book(2, "Sách 02", "Tác giả B"),
        Book(3, "Sách 03", "Tác giả C")
    )
    val students = mutableStateListOf(Student(1, "Nguyen Van A"), Student(2, "Tran Thi B"))

    fun addBook(title: String, author: String) { books.add(Book(books.size + 1, title, author)) }
    fun addStudent(name: String) { students.add(Student(students.size + 1, name)) }
}

// ================= GIAO DIỆN =================

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme { LibraryApp() } }
    }
}

@Composable
fun LibraryApp() {
    val nav = rememberNavController()
    val library = remember { Library() }
    val current = remember { mutableStateOf(library.students.first()) }
    val route = nav.currentBackStackEntryAsState().value?.destination?.route

    Scaffold(bottomBar = {
        NavigationBar {
            NavigationBarItem(route == "manage", { nav.navigate("manage") { launchSingleTop = true } },
                { Icon(Icons.Default.Home, null) }, label = { Text("Quản lý") })
            NavigationBarItem(route == "books", { nav.navigate("books") { launchSingleTop = true } },
                { Icon(Icons.Default.List, null) }, label = { Text("DS Sách") })
            NavigationBarItem(route == "students", { nav.navigate("students") { launchSingleTop = true } },
                { Icon(Icons.Default.Person, null) }, label = { Text("Sinh viên") })
        }
    }) { padding ->
        NavHost(nav, startDestination = "manage", modifier = Modifier.padding(padding)) {
            composable("manage") { ManageScreen(library, current) }
            composable("books") { BookListScreen(library) }
            composable("students") { StudentListScreen(library) }
        }
    }
}

@Composable
fun ManageScreen(library: Library, current: MutableState<Student>) {
    var name by remember { mutableStateOf(current.value.name) }
    val checked = remember { mutableStateListOf<Int>() }
    var refresh by remember { mutableStateOf(0) }

    Column(Modifier.padding(16.dp)) {
        Text("Hệ thống Quản lý Thư viện", fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally))
        Spacer(Modifier.height(16.dp))

        Text("Sinh viên")
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(name, { name = it }, Modifier.weight(1f), singleLine = true)
            Spacer(Modifier.width(8.dp))
            Button(onClick = {
                val found = library.students.find { it.name.equals(name, ignoreCase = true) }
                if (found != null) current.value = found
                else { library.addStudent(name); current.value = library.students.last() }
            }) { Text("Thay đổi") }
        }

        Spacer(Modifier.height(12.dp))
        Text("Danh sách sách")
        LazyColumn(Modifier.weight(1f)) {
            items(library.books) { b ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = b.id in checked,
                        onCheckedChange = { if (it) checked.add(b.id) else checked.remove(b.id) })
                    Text(b.describe())
                }
            }
        }

        Button(
            onClick = {
                library.books.filter { it.id in checked }.forEach { current.value.borrow(it) }
                checked.clear()
                refresh++
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) { Text("Thêm") }

        Spacer(Modifier.height(8.dp))
        key(refresh) {
            Text("${current.value.name} đã mượn:", fontWeight = FontWeight.Bold)
            current.value.borrowedBooks.forEach { Text("• ${it.describe()}") }
        }
    }
}

@Composable
fun BookListScreen(library: Library) {
    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    Column(Modifier.padding(16.dp)) {
        OutlinedTextField(title, { title = it }, label = { Text("Tên sách") })
        OutlinedTextField(author, { author = it }, label = { Text("Tác giả") })
        Button(onClick = {
            if (title.isNotBlank()) { library.addBook(title, author); title = ""; author = "" }
        }) { Text("Thêm sách") }
        LazyColumn { items(library.books) { Text(it.describe(), Modifier.padding(4.dp)) } }
    }
}

@Composable
fun StudentListScreen(library: Library) {
    var name by remember { mutableStateOf("") }
    Column(Modifier.padding(16.dp)) {
        OutlinedTextField(name, { name = it }, label = { Text("Tên sinh viên") })
        Button(onClick = {
            if (name.isNotBlank()) { library.addStudent(name); name = "" }
        }) { Text("Thêm sinh viên") }
        LazyColumn { items(library.students) { Text("SV #${it.id}: ${it.name}", Modifier.padding(4.dp)) } }
    }
}