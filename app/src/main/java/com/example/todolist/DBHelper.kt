package com.example.todolist

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "ToDoListApp.db"
        private const val DATABASE_VERSION = 1

        // Tabla de usuarios
        private const val TABLE_USERS = "users"
        private const val COLUMN_USER_ID = "id"
        private const val COLUMN_USERNAME = "username"
        private const val COLUMN_PASSWORD = "password"

        // Tabla de tareas
        private const val TABLE_TASKS = "tasks"
        private const val COLUMN_TASK_ID = "task_id"
        private const val COLUMN_TASK_NAME = "task_name"
        private const val COLUMN_TASK_COMPLETED = "task_completed"
        private const val COLUMN_TASK_ASSIGNED_TO = "assigned_to" // Usuario al que se le asigna la tarea

    }

    override fun onCreate(db: SQLiteDatabase) {
        // Crear tabla de usuarios
        val createUsersTable = ("CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USERNAME + " TEXT,"
                + COLUMN_PASSWORD + " TEXT" + ")")
        db.execSQL(createUsersTable)

        // Crear tabla de tareas
        val createTasksTable = ("CREATE TABLE " + TABLE_TASKS + "("
                + COLUMN_TASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_TASK_NAME + " TEXT,"
                + COLUMN_TASK_COMPLETED + " INTEGER,"
                + COLUMN_TASK_ASSIGNED_TO + " INTEGER" + ")")
        db.execSQL(createTasksTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Si la tabla ya existe, eliminarla
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TASKS")
        onCreate(db)
    }

    // Insertar un nuevo usuario en la base de datos
    fun addUser(user: User): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_USERNAME, user.username)
        values.put(COLUMN_PASSWORD, user.password)

        // Insertar fila
        val result = db.insert(TABLE_USERS, null, values)
        db.close()
        return result
    }

    // Verificar si el usuario existe en la base de datos
    fun checkUser(username: String, password: String): Boolean {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_USERS WHERE $COLUMN_USERNAME = ? AND $COLUMN_PASSWORD = ?"
        val cursor = db.rawQuery(query, arrayOf(username, password))

        val exists = cursor.count > 0
        cursor.close()
        db.close()

        return exists
    }

    // Funciones para manejar tareas

    // Insertar una nueva tarea
    fun addTask(taskName: String, userId: Int): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_TASK_NAME, taskName)
        values.put(COLUMN_TASK_COMPLETED, 0) // Tarea inicialmente incompleta
        values.put(COLUMN_TASK_ASSIGNED_TO, userId)

        val result = db.insert(TABLE_TASKS, null, values)
        db.close()
        return result
    }

    // Obtener todas las tareas asignadas a un usuario
    fun getTasksForUser(userId: Int): List<Task> {
        val taskList = ArrayList<Task>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_TASKS WHERE $COLUMN_TASK_ASSIGNED_TO = ?", arrayOf(userId.toString()))

        if (cursor.moveToFirst()) {
            do {
                val taskId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TASK_ID))
                val taskName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TASK_NAME))
                val taskCompleted = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TASK_COMPLETED)) == 1
                taskList.add(Task(taskId, taskName, taskCompleted))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return taskList
    }

    // Marcar tarea como completada
    fun updateTaskStatus(taskId: Int, isCompleted: Boolean) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_TASK_COMPLETED, if (isCompleted) 1 else 0)

        db.update(TABLE_TASKS, values, "$COLUMN_TASK_ID = ?", arrayOf(taskId.toString()))
        db.close()
    }

    // Eliminar tarea
    fun deleteTask(taskId: Int) {
        val db = this.writableDatabase
        db.delete(TABLE_TASKS, "$COLUMN_TASK_ID = ?", arrayOf(taskId.toString()))
        db.close()
    }

    // Método para actualizar una tarea existente
    fun updateTask(taskId: Int, taskName: String) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_TASK_NAME, taskName)

        db.update(TABLE_TASKS, values, "$COLUMN_TASK_ID = ?", arrayOf(taskId.toString()))
        db.close()
    }

    // Método para obtener una tarea por su ID
    fun getTaskById(taskId: Int): Task? {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_TASKS WHERE $COLUMN_TASK_ID = ?", arrayOf(taskId.toString()))

        return if (cursor.moveToFirst()) {
            val taskName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TASK_NAME))
            val taskCompleted = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TASK_COMPLETED)) == 1
            Task(taskId, taskName, taskCompleted)
        } else {
            null
        }.also {
            cursor.close()
            db.close()
        }
    }

}

// Clase de datos para manejar tareas
data class Task(val id: Int, val name: String, val completed: Boolean)





