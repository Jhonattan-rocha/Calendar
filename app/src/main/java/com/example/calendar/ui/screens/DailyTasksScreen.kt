// ui/screens/DailyTasksScreen.kt
package com.example.calendar.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.* // Certifique-se de ter este import para remember e derivedStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.calendar.data.Task
import com.example.calendar.ui.components.ConfirmationDialog
import com.example.calendar.ui.components.TaskFormDialog
import com.example.calendar.ui.components.TaskItem
import com.example.calendar.ui.viewmodel.TaskViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyTasksScreen(
    navController: NavController,
    taskViewModel: TaskViewModel,
    epochDay: Long // Data do dia selecionado passada como argumento
) {
    val date = LocalDate.ofEpochDay(epochDay)

    // 1. Coletamos o Flow de TODAS as tarefas do ViewModel em um State.
    // 'allTasksState' será atualizado sempre que a lista de tarefas no DB mudar.
    val allTasksState by taskViewModel.allTasks.collectAsState()

    // 2. Filtramos essa lista de tarefas baseados na data.
    // Usamos 'remember' com 'derivedStateOf' para otimizar:
    // - 'derivedStateOf' garante que a operação de filtro só será executada
    //   quando 'allTasksState' ou 'date' realmente mudarem.
    // - O 'by' delegado torna o acesso a 'tasksForThisDay' mais limpo.
    val tasksForThisDay by remember(allTasksState, date) {
        derivedStateOf {
            allTasksState.filter { it.dueDate == date }
        }
    }

    // Estados para os modais (iguais aos da CalendarScreen)
    var showCreateEditDialog by remember { mutableStateOf(false) }
    var taskToEdit by remember { mutableStateOf<Task?>(null) }
    var showDeleteConfirmationDialog by remember { mutableStateOf(false) }
    var taskToDelete by remember { mutableStateOf<Task?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tarefas para ${date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            if (tasksForThisDay.isEmpty()) {
                Text("Nenhuma tarefa para este dia.", modifier = Modifier.padding(8.dp))
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(tasksForThisDay) { task ->
                        TaskItem(
                            task = task,
                            onToggleComplete = { taskViewModel.toggleTaskCompletion(it) },
                            onDeleteClick = {
                                taskToDelete = it
                                showDeleteConfirmationDialog = true
                            },
                            onEditClick = {
                                taskToEdit = it
                                showCreateEditDialog = true
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Botão "Criar Nova Tarefa" também nesta tela
            Button(
                onClick = {
                    taskToEdit = null
                    showCreateEditDialog = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Criar Nova Tarefa")
            }
        }
    }

    // Modais (iguais aos da CalendarScreen)
    if (showCreateEditDialog) {
        TaskFormDialog(
            task = taskToEdit,
            selectedDate = date, // A data padrão é o dia atual da tela
            onDismiss = { showCreateEditDialog = false },
            onConfirm = { task ->
                if (task.id == 0) {
                    taskViewModel.addTask(task.description, task.dueDate)
                } else {
                    taskViewModel.updateTask(task)
                }
                showCreateEditDialog = false
            }
        )
    }

    if (showDeleteConfirmationDialog) {
        ConfirmationDialog(
            title = "Confirmar Exclusão",
            message = "Tem certeza que deseja deletar a tarefa '${taskToDelete?.description}'?",
            onConfirm = {
                taskToDelete?.let { taskViewModel.deleteTask(it) }
                showDeleteConfirmationDialog = false
                taskToDelete = null
            },
            onDismiss = {
                showDeleteConfirmationDialog = false
                taskToDelete = null
            }
        )
    }
}