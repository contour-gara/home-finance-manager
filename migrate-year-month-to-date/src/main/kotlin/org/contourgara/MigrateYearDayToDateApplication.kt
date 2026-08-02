package org.contourgara

import org.contourgara.domain.NewExpense
import org.contourgara.infrastructure.saveNewExpenses
import org.contourgara.infrastructure.selectOldExpenses
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

fun main() {
    setUpDatabase()

    transaction {
        selectOldExpenses()
            .also {
                println(it.count())
            }
            .onEach {
                println("${it.id}, ${it.year}, ${it.month}, ${it.memo.replace(oldChar = '\n', newChar = ' ')}, ${it.dateFromMemo()}")
            }
            .map {
                NewExpense.from(oldExpense = it)
            }
//            .also {
//                saveNewExpenses(newExpenses = it)
//            }
    }
}
