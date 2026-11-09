package com.example.baitap1tuan5

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

object ApiConfig {
    // Địa chỉ lấy từ slide (kiểm tra lại với khung cURL trên trang mock)
    const val BASE_URL = "https://share.tryapps.store/mock/teach"

    // Dán MOCK_API_TOKEN vào đây. Để trống = dùng dữ liệu mẫu offline.
    const val TOKEN = ""
}

object TaskApi {
    private val client = OkHttpClient()
    private val JSON_TYPE = "application/json; charset=utf-8".toMediaType()
    private val offline get() = ApiConfig.TOKEN.isBlank()
    private val fake = sampleTasks().toMutableList()

    private suspend fun call(method: String, path: String, body: JSONObject? = null): String =
        withContext(Dispatchers.IO) {
            val rb = when {
                body != null -> body.toString().toRequestBody(JSON_TYPE)
                method == "GET" || method == "DELETE" -> null
                else -> "".toRequestBody(JSON_TYPE)
            }
            val req = Request.Builder()
                .url(ApiConfig.BASE_URL + path)
                .header("Authorization", "Bearer ${ApiConfig.TOKEN}")
                .header("Accept", "application/json")
                .method(method, rb)
                .build()
            client.newCall(req).execute().use { r ->
                if (!r.isSuccessful) throw IOException("HTTP ${r.code}")
                r.body?.string().orEmpty()
            }
        }

    // GET /tasks
    suspend fun getTasks(): List<Task> {
        if (offline) { delay(600); return fake.toList() }
        val arr = JSONArray(call("GET", "/tasks"))
        return (0 until arr.length()).map { arr.getJSONObject(it).toTask() }
    }

    // GET /tasks/{id}
    suspend fun getTask(id: Int): Task {
        if (offline) { delay(300); return fake.first { it.id == id } }
        return JSONObject(call("GET", "/tasks/$id")).toTask()
    }

    // POST /tasks
    suspend fun create(t: Task) {
        if (offline) { delay(300); fake.add(t); return }
        call("POST", "/tasks", t.toJson())
    }

    // PATCH /tasks/{id}
    suspend fun update(t: Task) {
        if (offline) {
            delay(300)
            val i = fake.indexOfFirst { it.id == t.id }
            if (i >= 0) fake[i] = t
            return
        }
        call("PATCH", "/tasks/${t.id}", t.toJson())
    }

    // DELETE /tasks/{id}
    suspend fun delete(id: Int) {
        if (offline) { delay(300); fake.removeAll { it.id == id }; return }
        call("DELETE", "/tasks/$id")
    }
}
