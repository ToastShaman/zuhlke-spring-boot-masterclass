package com.github.toastshaman.springbootmasterclass.todo.domain

import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class CreateTodoRequest(
    @field:NotBlank
    val title: String,
    @field:NotNull
    @field:Valid
    val tasks: List<CreateTaskRequest>
) {
    companion object
}