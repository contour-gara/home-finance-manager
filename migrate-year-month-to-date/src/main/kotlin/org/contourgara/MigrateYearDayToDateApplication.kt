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
                if (it.haveyyyyMMdd()) {
//                    println(it)
                    return@forEach
                }
                if (it.haveSlashes()) {
//                    println(it)
                    return@forEach
                }
                if (it.haveSlash()) {
//                    println(it)
                    return@forEach
                }
                // 日付なし
                println(it)
            }
    }
}
