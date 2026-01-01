package org.contourgara

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.netty.EngineMain
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import org.contourgara.application.CreateExpenseUseCase
import org.contourgara.application.DeleteExpenseUseCase
import org.contourgara.infrastructure.client.ExpenseEventIdClientImpl
import org.contourgara.infrastructure.repository.ExpenseEventRepositoryImpl
import org.contourgara.infrastructure.repository.ExpenseRepositoryImpl
import org.contourgara.infrastructure.repository.ExpensesRepositoryImpl
import org.contourgara.presentation.configureExpenseRouting
import org.contourgara.presentation.configureGlobalExceptionHandler
import org.slf4j.event.Level

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
    install(CallLogging) {
        level = Level.DEBUG
    }

    val appConfig = AppConfig.from(applicationConfig = environment.config)

    setUpDatabase(appConfig = appConfig)
    configureGlobalExceptionHandler()
    configureExpenseRouting(
        createExpenseUseCase = CreateExpenseUseCase(
            expenseRepository = ExpenseRepositoryImpl,
            expenseEventIdClient = ExpenseEventIdClientImpl(appConfig = appConfig),
            expenseEventRepository = ExpenseEventRepositoryImpl,
            expensesRepository = ExpensesRepositoryImpl,
        ),
        deleteExpenseUseCase = DeleteExpenseUseCase(),
    )

    routing {
        route("/") {
            get {
                call.respondText { "Expenses API is running!" }
            }
        }
    }
}
