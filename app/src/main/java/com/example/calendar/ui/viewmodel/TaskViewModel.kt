// ui/viewmodel/TaskViewModel.kt
package com.example.calendar.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.calendar.data.Task
import com.example.calendar.data.TaskDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter // Adicione para formatar a data

class TaskViewModel(private val taskDao: TaskDao) : ViewModel() {

    // Estado para a data atualmente selecionada no calendário
    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    // Flow de tarefas para a data selecionada.
    // flatMapLatest: sempre que _selectedDate mudar, ele pega o novo Flow de tarefas para aquela data.
    @OptIn(ExperimentalCoroutinesApi::class)
    val tasksForSelectedDate: StateFlow<List<Task>> = _selectedDate
        .flatMapLatest { date -> taskDao.getTasksForDate(date) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Flow de todas as tarefas (ainda útil para ver tudo ou em outra tela)
    val allTasks: StateFlow<List<Task>> = taskDao.getAllTasks().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Funções para manipular a data selecionada
    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
    }

    fun goToNextMonth() {
        _selectedDate.value = _selectedDate.value.plusMonths(1)
    }

    fun goToPreviousMonth() {
        _selectedDate.value = _selectedDate.value.minusMonths(1)
    }

    // Função para adicionar uma nova tarefa
    // Agora aceita a data de vencimento
    fun addTask(description: String, dueDate: LocalDate) {
        viewModelScope.launch {
            val newTask = Task(description = description, dueDate = dueDate)
            taskDao.insert(newTask)
        }
    }

    // Função para atualizar uma tarefa (incluindo descrição e status)
    fun updateTask(task: Task) {
        viewModelScope.launch {
            taskDao.update(task)
        }
    }

    // Função para alternar o status de conclusão de uma tarefa
    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            taskDao.update(task.copy(isCompleted = !task.isCompleted))
        }
    }

    // Função para deletar uma tarefa
    fun deleteTask(task: Task) {
        viewModelScope.launch {
            taskDao.delete(task)
        }
    }
}

// Factory para criar a instância de TaskViewModel com o DAO
class TaskViewModelFactory(private val taskDao: TaskDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskViewModel(taskDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}