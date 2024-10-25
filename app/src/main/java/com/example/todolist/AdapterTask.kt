package com.example.todolist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.BaseAdapter

class AdapterTask(private val context: Context, private val tasks: List<Task>) : BaseAdapter() {

    override fun getCount(): Int {
        return tasks.size
    }

    override fun getItem(position: Int): Any {
        return tasks[position]
    }

    override fun getItemId(position: Int): Long {
        return tasks[position].id.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.taskitem, parent, false)

        val taskName = view.findViewById<TextView>(R.id.taskName)
        val taskCompleted = view.findViewById<CheckBox>(R.id.taskCompleted)

        val task = tasks[position]

        taskName.text = task.name
        taskCompleted.isChecked = task.completed

        return view
    }
}