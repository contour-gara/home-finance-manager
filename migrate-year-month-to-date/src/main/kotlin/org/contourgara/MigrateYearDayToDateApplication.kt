package org.contourgara

import org.contourgara.infrastructure.selectOldExpense
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

fun main() {
    setUpDatabase()

    transaction {
        selectOldExpense()
            .also {
                println(it.count())
            }
            .forEach {
//                println(it)
                if (it.haveSlashes()) {
                    println(it)
                }
            }
    }
}
