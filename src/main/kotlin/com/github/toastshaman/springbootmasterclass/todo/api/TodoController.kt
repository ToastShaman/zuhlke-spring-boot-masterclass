package com.github.toastshaman.springbootmasterclass.todo.api

import com.github.toastshaman.springbootmasterclass.todo.domain.CreateTodoRequest
import com.github.toastshaman.springbootmasterclass.todo.domain.Todo
import com.github.toastshaman.springbootmasterclass.todo.domain.asTodo
import com.github.toastshaman.springbootmasterclass.todo.store.JooqTodoStore
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class TodoController(private val store: JooqTodoStore) {

    @GetMapping("/todos")
    fun lists() = ResponseEntity.ok(mapOf("todos" to store.all()))

    @PostMapping("/todos")
    fun add(@Validated @RequestBody request: CreateTodoRequest): ResponseEntity<Todo<Int>> {
        val todoId = store.new(request.asTodo())
        val todo = store.getById(todoId)
        return ResponseEntity.ok(todo)
    }
}