package org.contourgara

import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.netty.EngineMain
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import org.contourgara.application.CreateExpenseUseCase
import org.contourgara.infrastructure.client.UlidClientImpl
import org.contourgara.infrastructure.repository.ExpenseEventRepositoryImpl
import org.contourgara.infrastructure.repository.ExpenseRepositoryImpl
import org.slf4j.event.Level

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
    install(CallLogging) {
        level = Level.DEBUG
    }

    install(ContentNegotiation) {
        json()
    }

    val appConfig = AppConfig.from(applicationConfig = environment.config)

    setUpDatabase(appConfig = appConfig)
    configureRouting(
        createExpenseUseCase = CreateExpenseUseCase(
            expenseRepository = ExpenseRepositoryImpl,
            ulidClient = UlidClientImpl(),
            expenseEventRepository = ExpenseEventRepositoryImpl,
        )
    )
}
