package org.contourgara

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class FinanceCoreApplication

fun main(args: Array<String>) {
    runApplication<FinanceCoreApplication>(*args)
}
