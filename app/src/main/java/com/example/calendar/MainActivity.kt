// MainActivity.kt
package com.example.calendar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.lifecycle.viewmodel.compose.viewModel // Importe viewModel
import com.example.calendar.database.AppDatabase
import com.example.calendar.ui.theme.CalendarTheme
import com.example.calendar.ui.viewmodel.TaskViewModel
import com.example.calendar.ui.viewmodel.TaskViewModelFactory
import com.example.calendar.ui.screens.CalendarScreen // Importe sua tela Calendar
import com.example.calendar.ui.screens.DailyTasksScreen // Importe sua tela DailyTasks
import com.example.calendar.utils.NotificationUtils

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(applicationContext)
        val taskDao = database.taskDao()

        val notificationUtils = NotificationUtils(applicationContext)
        notificationUtils.createNotificationChannel()

        setContent {
            CalendarTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Crie o NavController
                    val navController = rememberNavController()
                    // Crie o ViewModel uma vez e passe-o para as telas
                    val taskViewModel: TaskViewModel = viewModel(factory = TaskViewModelFactory(taskDao))

                    NavHost(navController = navController, startDestination = "calendar") {
                        // Rota para a tela do calendário
                        composable("calendar") {
                            CalendarScreen(navController = navController, taskViewModel = taskViewModel)
                        }
                        // Rota para a tela de tarefas diárias
                        // O argumento 'epochDay' passa a data selecionada
                        composable(
                            "dailyTasks/{epochDay}",
                            arguments = listOf(navArgument("epochDay") { type = NavType.LongType })
                        ) { backStackEntry ->
                            val epochDay = backStackEntry.arguments?.getLong("epochDay")
                            if (epochDay != null) {
                                DailyTasksScreen(
                                    navController = navController,
                                    taskViewModel = taskViewModel,
                                    epochDay = epochDay
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}