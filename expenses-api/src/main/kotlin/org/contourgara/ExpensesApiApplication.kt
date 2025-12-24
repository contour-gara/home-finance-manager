package org.contourgara

import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.netty.EngineMain
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import org.contourgara.application.CreateExpenseUseCase
import org.contourgara.infrastructure.client.UlidClientImpl
import org.contourgara.infrastructure.repository.ExpenseEventRepositoryImpl
import org.contourgara.infrastructure.repository.ExpenseRepositoryImpl
import org.contourgara.presentation.configureExpenseRouting
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
    configureExpenseRouting(
        createExpenseUseCase = CreateExpenseUseCase(
            expenseRepository = ExpenseRepositoryImpl,
            ulidClient = UlidClientImpl(),
            expenseEventRepository = ExpenseEventRepositoryImpl,
        )
    )

    routing {
        route("/") {
            get {
                call.respondText { "Expenses API is running!" }
            }
        }
    }
}
