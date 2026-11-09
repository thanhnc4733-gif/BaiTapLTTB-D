package com.example.baitap1tuan5

import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class SubTask(val title: String, val done: Boolean = false)

data class Task(
    val id: Int = 0,
    val title: String = "",
    val note: String = "",
    val category: String = "study",   // study | life
    val date: String = "",            // yyyy-MM-dd
    val time: String = "",            // HH:mm
    val priority: String = "medium",  // high | medium | low
    val remind: Boolean = false,
    val done: Boolean = false,
    val subtasks: List<SubTask> = emptyList(),
    val place: String = "",           // gắn từ quyền Vị trí
    val assignee: String = ""         // chọn từ quyền Danh bạ
)

fun JSONObject.toTask(): Task {
    val subs = optJSONArray("subtasks") ?: JSONArray()
    return Task(
        id = optInt("id"),
        title = optString("title"),
        note = optString("note"),
        category = optString("category", "study"),
        date = optString("date"),
        time = optString("time"),
        priority = optString("priority", "medium"),
        remind = optBoolean("remind"),
        done = optBoolean("done"),
        subtasks = (0 until subs.length()).map {
            val o = subs.getJSONObject(it)
            SubTask(o.optString("title"), o.optBoolean("done"))
        },
        place = optString("place"),
        assignee = optString("assignee")
    )
}

fun Task.toJson(): JSONObject {
    val arr = JSONArray()
    subtasks.forEach { arr.put(JSONObject().put("title", it.title).put("done", it.done)) }
    return JSONObject()
        .put("id", id)
        .put("title", title)
        .put("note", note)
        .put("category", category)
        .put("date", date)
        .put("time", time)
        .put("priority", priority)
        .put("remind", remind)
        .put("done", done)
        .put("subtasks", arr)
        .put("place", place)
        .put("assignee", assignee)
}

fun priorityLabel(p: String) = when (p) {
    "high" -> "Ưu tiên cao"
    "low" -> "Ưu tiên thấp"
    else -> "Ưu tiên vừa"
}

fun todayStr(offsetDays: Int = 0): String {
    val c = Calendar.getInstance()
    c.add(Calendar.DAY_OF_YEAR, offsetDays)
    return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(c.time)
}

fun friendlyDate(date: String, time: String): String {
    val d = when (date) {
        todayStr(0) -> "Hôm nay"
        todayStr(1) -> "Ngày mai"
        else -> date
    }
    return if (time.isBlank()) d else "$d, $time"
}

fun todayTitle(): String {
    val c = Calendar.getInstance()
    val days = listOf("Chủ nhật", "Thứ Hai", "Thứ Ba", "Thứ Tư", "Thứ Năm", "Thứ Sáu", "Thứ Bảy")
    return "${days[c.get(Calendar.DAY_OF_WEEK) - 1]}, ${c.get(Calendar.DAY_OF_MONTH)} tháng ${c.get(Calendar.MONTH) + 1}"
}

fun initialsOf(name: String): String =
    name.split(" ").mapNotNull { it.firstOrNull()?.uppercase() }
        .takeLast(2).joinToString("").ifEmpty { "?" }

// Dữ liệu mẫu giống slide (dùng khi chưa có token API)
fun sampleTasks(): List<Task> = listOf(
    Task(4, "Tập thể dục 30 phút", "", "life", todayStr(0), "06:30", "medium", true, true),
    Task(
        1, "Nộp bài tập Lập trình di động",
        "Nộp file .zip lên hệ thống của trường. Nhớ kèm ảnh chụp màn hình app.",
        "study", todayStr(0), "09:00", "high", true, true,
        listOf(
            SubTask("Hoàn thiện màn hình đăng nhập", true),
            SubTask("Chụp ảnh màn hình app", true),
            SubTask("Nén file và nộp bài", true)
        )
    ),
    Task(
        2, "Ôn chương 3 – Cấu trúc dữ liệu", "Tập trung phần cây nhị phân và bảng băm.",
        "study", todayStr(0), "14:00", "medium", true, false,
        listOf(SubTask("Đọc lại slide bài giảng", true), SubTask("Làm 10 bài tập cuối chương"))
    ),
    Task(3, "Đi chợ mua đồ cho tuần", "", "life", todayStr(0), "17:30", "low", false, false),
    Task(5, "Gọi điện về nhà", "", "life", todayStr(1), "21:00", "low", true, false),
    Task(6, "Họp nhóm đồ án", "", "study", todayStr(2), "20:00", "high", true, false)
)
