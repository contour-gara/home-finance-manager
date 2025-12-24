package org.contourgara.presentation

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import kotlinx.serialization.SerializationException
import org.contourgara.application.CreateExpenseUseCase

fun Application.configureExpenseRouting(
    createExpenseUseCase: CreateExpenseUseCase,
) {
    routing {
        route("/expense") {
            post {
                try {
                    val createExpenseRequest = call.receive<CreateExpenseRequest>()
                    val result = createExpenseUseCase.execute(createExpenseRequest.toParam())
                    call.respond(
                        status = HttpStatusCode.Created,
                        message = CreateExpenseResponse(
                            expenseId = result.first.id,
                            expenseEventId = result.second.id,
                        ),
                    )
                } catch (e: SerializationException) {
                    call.respond(HttpStatusCode.BadRequest, e.message.toString())
                }
            }
        }
    }
}
