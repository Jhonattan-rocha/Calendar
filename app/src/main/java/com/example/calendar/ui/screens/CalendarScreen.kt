// ui/screens/CalendarScreen.kt
package com.example.calendar.ui.screens

import android.R
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.calendar.data.Task
import com.example.calendar.ui.components.CalendarView
import com.example.calendar.ui.components.ConfirmationDialog
import com.example.calendar.ui.components.TaskFormDialog
import com.example.calendar.ui.components.TaskItem
import com.example.calendar.ui.viewmodel.TaskViewModel // Importe seu ViewModel
import com.example.calendar.ui.viewmodel.EnumTaskViewMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    navController: NavController,
    taskViewModel: TaskViewModel
) {
    val selectedDate by taskViewModel.selectedDate.collectAsState()
    val tasksForSelectedDate by taskViewModel.tasksForSelectedDate.collectAsState()
    val currentViewMode by taskViewModel.currentViewModel.collectAsState()

    // Estados para os modais
    var showCreateEditDialog by remember { mutableStateOf(false) }
    var taskToEdit by remember { mutableStateOf<Task?>(null) } // Tarefa sendo editada (null para criar)
    var showDeleteConfirmationDialog by remember { mutableStateOf(false) }
    var taskToDelete by remember { mutableStateOf<Task?>(null) } // Tarefa sendo deletada

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meu Calendário de Tarefas") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                taskToEdit = null // Para criar, não há tarefa para editar
                showCreateEditDialog = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar Tarefa")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            CalendarView(
                currentMonth = selectedDate, // Usa a data selecionada como mês atual
                selectedDate = selectedDate,
                onDateSelected = { taskViewModel.selectDate(it) },
                onMonthChanged = { taskViewModel.selectDate(it) }
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text("Tarefas para ${selectedDate.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))}:",
                style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                EnumTaskViewMode.entries.forEach { mode ->
                    FilterChip(
                        selected = currentViewMode == mode,
                        onClick = { taskViewModel.setViewMode(mode) },
                        label = { Text(mode.name.replace("_", " ")) },
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }

            // Prévia das Tarefas (até 3, por exemplo)
            if (tasksForSelectedDate.isEmpty()) {
                Text("Nenhuma tarefa para este dia.", modifier = Modifier.padding(start = 8.dp))
            } else {
                when (currentViewMode){
                    EnumTaskViewMode.LIST -> {
                        LazyColumn(
                            modifier = Modifier.heightIn(max = 200.dp) // Altura limitada para prévia
                        ) {
                            items(tasksForSelectedDate.take(3)) { task -> // Mostra até 3 tarefas
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
                        if (tasksForSelectedDate.size > 3) {
                            TextButton(
                                onClick = { navController.navigate("dailyTasks/${selectedDate.toEpochDay()}") },
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text("Ver todas as ${tasksForSelectedDate.size} tarefas")
                            }
                        }
                    }
                    EnumTaskViewMode.COMPACT_LIST -> {
                        LazyColumn(
                            modifier = Modifier.heightIn(max = 200.dp)
                        ) {
                            items(tasksForSelectedDate.take(3)) { task ->
                                Card(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = task.description,
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = if (task.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                                        )
                                        Checkbox(
                                            checked = task.isCompleted,
                                            onCheckedChange = { taskViewModel.toggleTaskCompletion(task) }
                                        )
                                    }
                                }
                            }
                        }
                        if (tasksForSelectedDate.size > 3) {
                            TextButton(
                                onClick = { navController.navigate("dailyTasks/${selectedDate.toEpochDay()}") },
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text(
                                    text = "Ver todas as ${tasksForSelectedDate.size} tarefas"
                                )
                            }
                        }
                    }
                    EnumTaskViewMode.GRID -> {
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            items(tasksForSelectedDate) { task ->
                                Card(
                                    modifier = Modifier.width(160.dp).heightIn(min = 80.dp),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp).fillMaxWidth(),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = task.description,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                        Text(
                                            text = if (task.isCompleted) "Concluido" else "Pendente",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = if (task.isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceAround
                                        ) {
                                            IconButton(onClick = {
                                                taskToEdit = task
                                                showCreateEditDialog = true
                                            }) {
                                                Icon(Icons.Default.Edit, contentDescription = "Editar")
                                            }
                                            IconButton(onClick = {
                                                taskToDelete = task
                                                showDeleteConfirmationDialog = true
                                            }) {
                                                Icon(Icons.Default.Delete, contentDescription = "Deletar")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modais
    if (showCreateEditDialog) {
        TaskFormDialog(
            task = taskToEdit,
            selectedDate = selectedDate,
            onDismiss = { showCreateEditDialog = false },
            onConfirm = { task ->
                if (task.id == 0) { // Nova tarefa
                    taskViewModel.addTask(task.description, task.dueDate)
                } else { // Editar tarefa
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