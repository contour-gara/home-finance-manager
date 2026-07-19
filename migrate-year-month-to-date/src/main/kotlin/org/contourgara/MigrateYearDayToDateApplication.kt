package org.contourgara

import org.contourgara.infrastructure.selectOldExpenses
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

fun main() {
    setUpDatabase()

    transaction {
        selectOldExpenses()
            .also {
                println(it.count())
            }
            .forEach {
                if (it.haveyyyyMMdd()) {
                    println(it)
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
//                println(it)
            }
    }
}
