package com.github.toastshaman.springbootmasterclass.todo.domain

import com.github.toastshaman.springbootmasterclass.todo.jooq.tables.records.TodoListRecord
import com.github.toastshaman.springbootmasterclass.todo.jooq.tables.records.TodoTaskRecord

data class Todo<Key>(
    val id: Key,
    val title: String,
    val tasks: List<Task<Key>>
)

data class Task<Key>(
    val id: Key,
    val description: String
)

fun CreateTodoRequest.asTodo() = Todo(
    id = null,
    title = title,
    tasks = tasks.map(CreateTaskRequest::asTask)
)

fun CreateTaskRequest.asTask() = Task(
    id = null,
    description = description
)

fun TodoListRecord.asTodo(tasks: List<Task<Int>>) = Todo(
    id = id!!,
    title = title.orEmpty(),
    tasks = tasks
)

fun TodoTaskRecord.asTask() = Task(
    id = id!!,
    description = description.orEmpty()
)

fun Todo<Nothing?>.asRecord() = TodoListRecord(
    id = null,
    title = title
)

fun Task<Nothing?>.asRecordWithId(id: Int) = TodoTaskRecord(
    id = null,
    todoListId = id,
    description = description
)
