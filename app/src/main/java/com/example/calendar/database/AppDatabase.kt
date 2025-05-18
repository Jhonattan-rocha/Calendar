// data/AppDatabase.kt
package com.example.calendar.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters // Importe esta anotação

import com.example.calendar.data.Task
import com.example.calendar.data.TaskDao
import com.example.calendar.data.Converters

@Database(
    entities = [Task::class],
    version = 1, // Mantenha 1 se for a primeira vez que você está adicionando LocalDate
    exportSchema = false
)
@TypeConverters(Converters::class) // Registre seu TypeConverter aqui
abstract class AppDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var Instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, AppDatabase::class.java, "todo_database")
                    .fallbackToDestructiveMigration(dropAllTables = false) // Use isso durante o desenvolvimento
                    .addTypeConverter(Converters()) // Adicione a instância do seu conversor
                    .build()
                    .also { Instance = it }
            }
        }
    }
}