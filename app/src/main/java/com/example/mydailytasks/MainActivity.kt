package com.example.mydailytasks

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MainActivity : AppCompatActivity() {

    private lateinit var taskEditText: EditText
    private lateinit var addTaskButton: Button
    private lateinit var tasksRecyclerView: RecyclerView
    private lateinit var taskAdapter: TaskAdapter
    private val tasks = mutableListOf<String>()

    private val PREFS_NAME = "MyDailyTasksPrefs"
    private val TASKS_KEY = "tasks"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        taskEditText = findViewById(R.id.taskEditText)
        addTaskButton = findViewById(R.id.addTaskButton)
        tasksRecyclerView = findViewById(R.id.tasksRecyclerView)

        loadTasks()

        taskAdapter = TaskAdapter(tasks) {
            deleteTask(it)
        }
        tasksRecyclerView.layoutManager = LinearLayoutManager(this)
        tasksRecyclerView.adapter = taskAdapter

        addTaskButton.setOnClickListener {
            addTask()
        }
    }

    private fun addTask() {
        val taskText = taskEditText.text.toString().trim()
        if (taskText.isNotEmpty()) {
            tasks.add(taskText)
            taskAdapter.notifyItemInserted(tasks.size - 1)
            taskEditText.text.clear()
            saveTasks()
        }
    }

    private fun deleteTask(task: String) {
        val position = tasks.indexOf(task)
        if (position != -1) {
            tasks.removeAt(position)
            taskAdapter.notifyItemRemoved(position)
            saveTasks()
        }
    }

    private fun saveTasks() {
        val sharedPrefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        val json = Gson().toJson(tasks)
        editor.putString(TASKS_KEY, json)
        editor.apply()
    }

    private fun loadTasks() {
        val sharedPrefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = sharedPrefs.getString(TASKS_KEY, null)
        if (json != null) {
            val type = object : TypeToken<MutableList<String>>() {}.type
            tasks.addAll(Gson().fromJson(json, type))
        }
    }
}

