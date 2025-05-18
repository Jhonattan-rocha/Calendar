// ui/components/TaskFormDialog.kt
package com.example.calendar.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.calendar.data.Task // Importe sua entidade Task
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class) // Para OutlinedTextField
@Composable
fun TaskFormDialog(
    task: Task?, // Tarefa a ser editada (null para nova tarefa)
    selectedDate: LocalDate, // Data padrão para novas tarefas
    onDismiss: () -> Unit,
    onConfirm: (Task) -> Unit
) {
    var description by remember { mutableStateOf(task?.description ?: "") }
    var isCompleted by remember { mutableStateOf(task?.isCompleted ?: false) }
    var dueDate by remember { mutableStateOf(task?.dueDate ?: selectedDate) } // Usa a data selecionada como padrão

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (task == null) "Nova Tarefa" else "Editar Tarefa") },
        text = {
            Column {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descrição da Tarefa") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Text("Concluída:")
                    Checkbox(
                        checked = isCompleted,
                        onCheckedChange = { isCompleted = it }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                // Simplificado: para selecionar data, idealmente seria um DatePicker
                // Por enquanto, apenas mostra a data e permite alteração simples
                Text("Data: ${dueDate.format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE)}")
                Button(onClick = { /* TODO: Implementar DatePicker para mudar dueDate */ }) {
                    Text("Mudar Data (TODO)")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (description.isNotBlank()) {
                        val updatedTask = task?.copy(
                            description = description,
                            isCompleted = isCompleted,
                            dueDate = dueDate
                        ) ?: Task(description = description, isCompleted = isCompleted, dueDate = dueDate)
                        onConfirm(updatedTask)
                    }
                }
            ) {
                Text(if (task == null) "Criar" else "Salvar")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}