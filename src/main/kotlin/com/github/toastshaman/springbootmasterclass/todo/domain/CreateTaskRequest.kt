package com.github.toastshaman.springbootmasterclass.todo.domain

import javax.validation.constraints.NotBlank

data class CreateTaskRequest(
    @field:NotBlank
    val description: String
) {
    companion object
}