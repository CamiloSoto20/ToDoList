package com.example.todolist

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class TaskListActivity : AppCompatActivity() {

    lateinit var db: DBHelper
    lateinit var taskListView: ListView
    lateinit var taskAdapter: AdapterTask
    lateinit var addTaskButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.listatareas)

        db = DBHelper(this)

        val userId = intent.getIntExtra("USER_ID", -1)
        val taskList = db.getTasksForUser(userId)

        taskListView = findViewById(R.id.taskListView)
        taskAdapter = AdapterTask(this, taskList)
        taskListView.adapter = taskAdapter

        addTaskButton = findViewById(R.id.addTaskButton)
        addTaskButton.setOnClickListener {
            val intent = Intent(this, EditTask::class.java)
            intent.putExtra("USER_ID", userId) // Pasar el ID del usuario actual
            startActivity(intent)
        }

        taskListView.setOnItemClickListener { _, _, position, _ ->
            val task = taskList[position]
            val intent = Intent(this, EditTask::class.java)
            intent.putExtra("TASK_ID", task.id)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        // Actualizar la lista de tareas cuando regrese a esta actividad
        val userId = intent.getIntExtra("USER_ID", -1)
        val taskList = db.getTasksForUser(userId)
        taskAdapter = AdapterTask(this, taskList)
        taskListView.adapter = taskAdapter
    }
}
