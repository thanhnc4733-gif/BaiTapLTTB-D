package com.example.baitap1tuan5

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

data class TodoTask(
    val id: String = "",
    val title: String = "",
    val category: String = "Học tập",
    val time: String = "",
    val done: Boolean = false
)

class TaskViewModel : ViewModel() {
    private var ref: DatabaseReference? = null
    private var listener: ValueEventListener? = null

    var tasks by mutableStateOf<List<TodoTask>>(emptyList())
        private set

    fun start() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        stop()
        val r = FirebaseDatabase.getInstance().getReference("users/$uid/tasks")
        ref = r
        listener = object : ValueEventListener {
            override fun onDataChange(s: DataSnapshot) {
                tasks = s.children
                    .mapNotNull { it.getValue(TodoTask::class.java) }
                    .sortedBy { it.time }
            }
            override fun onCancelled(e: DatabaseError) {}
        }
        r.addValueEventListener(listener!!)
    }

    fun stop() {
        listener?.let { ref?.removeEventListener(it) }
        listener = null
        ref = null
        tasks = emptyList()
    }

    fun add(title: String, category: String, time: String) {
        val r = ref ?: return
        val id = r.push().key ?: return
        r.child(id).setValue(TodoTask(id, title, category, time, false))
    }

    fun toggle(t: TodoTask) { ref?.child(t.id)?.child("done")?.setValue(!t.done) }
    fun delete(t: TodoTask) { ref?.child(t.id)?.removeValue() }

    override fun onCleared() { stop() }
}