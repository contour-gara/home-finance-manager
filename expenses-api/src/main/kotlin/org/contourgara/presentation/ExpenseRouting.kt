package org.contourgara.presentation

import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.delete
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import kotlinx.serialization.Serializable
import org.contourgara.application.CreateExpenseUseCase
import org.contourgara.application.DeleteExpenseUseCase
import ulid.ULID

fun Application.configureExpenseRouting(
    createExpenseUseCase: CreateExpenseUseCase,
    deleteExpenseUseCase: DeleteExpenseUseCase,
) {
    install(ContentNegotiation) {
        json()
    }

    routing {
        route("/expense") {
            post {
                call
                    .receive<CreateExpenseRequest>()
                    .toParam()
                    .let {
                        createExpenseUseCase
                            .execute(
                                param = it,
                            )
                    }.also {
                        call.respond(
                            status = HttpStatusCode.Created,
                            message = CreateExpenseResponse.from(createExpenseDto = it),
                        )
                    }
            }

            delete("{expenseId}") {
                call
                    .pathParameters["expenseId"]
                    ?.let { deleteExpenseUseCase.execute(expenseId = it) }
                    ?.let {
                        call.respond(
                            status = HttpStatusCode.OK,
                            message = DeleteExpenseResponse(expenseEventId = it),
                        )
                    }
                    ?: throw RuntimeException("path parameter was not found: expenseId")
            }
        }
    }
}

@Serializable
data class DeleteExpenseResponse(
    private val expenseEventId: ULID,
)
