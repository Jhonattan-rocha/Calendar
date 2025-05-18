// ui/components/TaskItem.kt
package com.example.calendar.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.calendar.data.Task // Importe sua entidade Task

@Composable
fun TaskItem(
    task: Task,
    onToggleComplete: (Task) -> Unit,
    onDeleteClick: (Task) -> Unit,
    onEditClick: (Task) -> Unit // Novo callback para editar
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onToggleComplete(task) }, // Torna o card clicável para alternar o status
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween // Espaçamento entre os itens
        ) {
            Column(modifier = Modifier.weight(1f)) { // Coluna para descrição e data
                Text(
                    text = task.description,
                    style = if (task.isCompleted) {
                        MaterialTheme.typography.bodyLarge.copy(textDecoration = TextDecoration.LineThrough)
                    } else {
                        MaterialTheme.typography.bodyLarge
                    }
                )
                Text(
                    text = "Data: ${task.dueDate.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }


            Spacer(modifier = Modifier.width(8.dp))

            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { onToggleComplete(task) }
            )
            Spacer(modifier = Modifier.width(8.dp))

            // Botão de Editar
            IconButton(onClick = { onEditClick(task) }) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Editar Tarefa",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.width(4.dp))

            // Botão de Deletar
            IconButton(onClick = { onDeleteClick(task) }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Deletar Tarefa",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}