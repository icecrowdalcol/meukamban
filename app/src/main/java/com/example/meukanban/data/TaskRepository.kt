package com.example.meukanban.data

import com.example.meukanban.data.model.Status
import com.example.meukanban.data.model.Task

/**
 * Simple in-memory repository shared by every screen so tasks created,
 * edited, moved or removed in one tab are reflected in the others.
 */
object TaskRepository {

    private var nextId = 12
    private val tasks = mutableListOf(
        Task("0", "Criar nova tela do app", Status.TODO),
        Task("1", "Validar informações na tela de login", Status.TODO),
        Task("2", "Adicionar nova funcionalidade no app", Status.TODO),
        Task("3", "Salvar token localmente", Status.TODO),
        Task("4", "Criar funcionalidade de logout no app", Status.TODO),
        Task("5", "Terminar o MeuKanban", Status.DOING),
        Task("6", "Passar de ano", Status.DOING),
        Task("7", "Corrigir bug no Parcelize", Status.DOING),
        Task("8", "Atualizar o Android Studio", Status.DONE),
        Task("9", "Acompanhar o Google I/O", Status.DONE),
        Task("10", "Keep Android Open", Status.DONE),
        Task("11", "Consertar leitor de PDF", Status.DONE)
    )

    fun getByStatus(status: Status): List<Task> = tasks.filter { it.status == status }

    fun add(description: String, status: Status) {
        tasks.add(Task(id = (nextId++).toString(), description = description, status = status))
    }

    fun update(task: Task) {
        val index = tasks.indexOfFirst { it.id == task.id }
        if (index != -1) {
            tasks[index] = task
        }
    }

    fun remove(task: Task) {
        tasks.removeAll { it.id == task.id }
    }

    fun move(task: Task, newStatus: Status) {
        update(task.copy(status = newStatus))
    }

    fun moveForward(task: Task) {
        val next = when (task.status) {
            Status.TODO -> Status.DOING
            Status.DOING -> Status.DONE
            Status.DONE -> Status.DONE
        }
        move(task, next)
    }

    fun moveBack(task: Task) {
        val previous = when (task.status) {
            Status.TODO -> Status.TODO
            Status.DOING -> Status.TODO
            Status.DONE -> Status.DOING
        }
        move(task, previous)
    }
}
