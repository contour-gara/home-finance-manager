package org.contourgara.presentation

import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
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
    install(ContentNegotiation) {
        json()
    }

    routing {
        route("/expense") {
            post {
                try {
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
                } catch (e: SerializationException) {
                    call.respond(HttpStatusCode.BadRequest, e.message.toString())
                }
            }
        }
    }
}
