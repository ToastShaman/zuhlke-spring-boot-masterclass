package com.github.toastshaman.springbootmasterclass.todo.store

@JvmInline
value class TodoId(val value: Int) {
    init {
        check(value > 0)
    }
}