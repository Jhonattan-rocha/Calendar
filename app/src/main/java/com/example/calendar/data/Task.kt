// data/Task.kt
package com.example.calendar.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate // Importe LocalDate para representar a data

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "task_description")
    val description: String,
    @ColumnInfo(name = "is_completed")
    val isCompleted: Boolean = false,
    @ColumnInfo(name = "due_date") // Novo campo para a data da tarefa
    val dueDate: LocalDate = LocalDate.now() // Usaremos LocalDate para a data
)