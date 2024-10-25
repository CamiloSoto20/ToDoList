package com.example.todolist

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AddEditTaskActivity : AppCompatActivity() {

    lateinit var taskNameEditText: EditText
    lateinit var saveTaskButton: Button
    lateinit var db: DBHelper
    var taskId: Int = -1
    var isEditMode = false
    lateinit var notificationHelper: Notificacion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tareasae)

        taskNameEditText = findViewById(R.id.taskNameEditText)
        saveTaskButton = findViewById(R.id.saveTaskButton)
        db = DBHelper(this)
        notificationHelper = Notificacion(this)

        // Ver si estamos en modo de edición
        taskId = intent.getIntExtra("TASK_ID", -1)
        if (taskId != -1) {
            isEditMode = true
            loadTask(taskId)
        }

        saveTaskButton.setOnClickListener {
            val taskName = taskNameEditText.text.toString()
            if (taskName.isNotEmpty()) {
                if (isEditMode) {
                    // Actualizar tarea existente
                    db.updateTask(taskId, taskName)
                    Toast.makeText(this, "Tarea actualizada", Toast.LENGTH_SHORT).show()
                } else {
                    // Añadir nueva tarea
                    val userId = intent.getIntExtra("USER_ID", -1)
                    db.addTask(taskName, userId)
                    notificationHelper.sendTaskNotification(taskName) // Enviar notificación
                    Toast.makeText(this, "Tarea añadida", Toast.LENGTH_SHORT).show()
                }
                finish() // Cierra la actividad
            } else {
                Toast.makeText(this, "Por favor, ingresa un nombre de tarea", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Cargar los detalles de la tarea existente si estamos en modo de edición
    private fun loadTask(taskId: Int) {
        val task = db.getTaskById(taskId)
        task?.let {
            taskNameEditText.setText(it.name)
        }
    }
}
