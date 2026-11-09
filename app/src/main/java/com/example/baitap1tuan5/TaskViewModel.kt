package com.example.baitap1tuan5

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class TaskViewModel : ViewModel() {
    var tasks by mutableStateOf<List<Task>>(emptyList())
        private set
    var loading by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set
    var notice by mutableStateOf<String?>(null)
        private set

    private var loaded = false

    fun clearNotice() { notice = null }

    fun reset() {
        tasks = emptyList()
        loaded = false
        error = null
    }

    // GET /tasks
    fun load(force: Boolean = false) {
        if (loading || (loaded && !force)) return
        loading = true
        error = null
        viewModelScope.launch {
            try {
                tasks = TaskApi.getTasks()
                loaded = true
            } catch (e: Exception) {
                error = "Không tải được dữ liệu: ${e.localizedMessage}"
            }
            loading = false
        }
    }

    // GET /tasks/{id}
    fun refresh(id: Int) {
        viewModelScope.launch {
            try {
                val t = TaskApi.getTask(id)
                if (t.id == id) {
                    tasks = tasks.map {
                        if (it.id == id) t.copy(place = it.place, assignee = it.assignee) else it
                    }
                }
            } catch (e: Exception) {
                // bỏ qua, vẫn dùng dữ liệu đang có
            }
        }
    }

    // Cập nhật giao diện trước, gọi API sau; lỗi thì hoàn tác
    private fun mutate(newList: List<Task>, call: suspend () -> Unit) {
        val old = tasks
        tasks = newList
        viewModelScope.launch {
            try {
                call()
            } catch (e: Exception) {
                tasks = old
                notice = "Lỗi mạng, đã hoàn tác: ${e.localizedMessage}"
            }
        }
    }

    fun add(t: Task) {
        val n = t.copy(id = (tasks.maxOfOrNull { it.id } ?: 0) + 1)
        mutate(tasks + n) { TaskApi.create(n) }
    }

    fun update(t: Task) =
        mutate(tasks.map { if (it.id == t.id) t else it }) { TaskApi.update(t) }

    fun toggle(t: Task) = update(t.copy(done = !t.done))

    fun delete(id: Int) =
        mutate(tasks.filter { it.id != id }) { TaskApi.delete(id) }

    fun toggleSub(t: Task, i: Int) = update(
        t.copy(subtasks = t.subtasks.mapIndexed { idx, s ->
            if (idx == i) s.copy(done = !s.done) else s
        })
    )

    fun addSub(t: Task, title: String) =
        update(t.copy(subtasks = t.subtasks + SubTask(title)))

    fun deleteSub(t: Task, i: Int) = update(
        t.copy(subtasks = t.subtasks.filterIndexed { idx, _ -> idx != i })
    )
}
