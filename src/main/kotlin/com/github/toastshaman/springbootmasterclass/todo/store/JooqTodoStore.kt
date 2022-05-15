package com.github.toastshaman.springbootmasterclass.todo.store

import com.github.toastshaman.springbootmasterclass.todo.domain.Todo
import com.github.toastshaman.springbootmasterclass.todo.domain.asRecord
import com.github.toastshaman.springbootmasterclass.todo.domain.asRecordWithId
import com.github.toastshaman.springbootmasterclass.todo.domain.asTask
import com.github.toastshaman.springbootmasterclass.todo.domain.asTodo
import com.github.toastshaman.springbootmasterclass.todo.jooq.tables.records.TodoListRecord
import com.github.toastshaman.springbootmasterclass.todo.jooq.tables.records.TodoTaskRecord
import com.github.toastshaman.springbootmasterclass.todo.jooq.tables.references.TODO_LIST
import com.github.toastshaman.springbootmasterclass.todo.jooq.tables.references.TODO_TASK
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.stereotype.Service

@Service
class JooqTodoStore(
    private val context: DSLContext
) {
    fun all() = context
        .select()
        .from(TODO_LIST)
        .join(TODO_TASK).on(TODO_LIST.ID.eq(TODO_TASK.TODO_LIST_ID))
        .fetchGroups(
            { it.into(TODO_LIST).into(TodoListRecord::class.java) },
            { it.into(TODO_TASK).into(TodoTaskRecord::class.java) }
        ).map { (k, v) -> k.asTodo(v.map { it.asTask() }) }

    fun new(todo: Todo<Nothing?>): TodoId = context.transactionResult { config ->
        val todoId = DSL.using(config)
            .insertInto(TODO_LIST)
            .set(todo.asRecord())
            .returning(TODO_LIST.ID)
            .fetchOne()!!.id!!

        DSL.using(config)
            .batchInsert(todo.tasks.map { it.asRecordWithId(todoId) })
            .execute()

        TodoId(todoId)
    }

    fun getById(id: TodoId) = context
        .select()
        .from(TODO_LIST)
        .join(TODO_TASK).on(TODO_LIST.ID.eq(TODO_TASK.TODO_LIST_ID))
        .where(TODO_LIST.ID.eq(id.value))
        .fetchGroups(
            { it.into(TODO_LIST).into(TodoListRecord::class.java) },
            { it.into(TODO_TASK).into(TodoTaskRecord::class.java) }
        )
        .map { (k, v) -> k.asTodo(v.map { it.asTask() }) }
        .first()
}
