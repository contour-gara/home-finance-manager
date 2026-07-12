package org.contourgara.presentation

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.util.logging.error
import org.contourgara.application.nextUlidByStateful
import ulid.ULID

fun Application.configureRouting(generateNextUlid: () -> ULID) {
    routing {
        get("/health") {
            call.respondText("Hello World!")
        }

        get("/next-ulid") {
            call.respondText(
                nextUlidByStateful(
                    generateNextUlid = generateNextUlid,
                ).toString()
            )
        }
    }

    install(plugin = StatusPages) {
        exception<Throwable> { call, cause ->
            call.application.environment.log.error(exception = cause)
            call.respondText(
                status = HttpStatusCode.InternalServerError,
                text = cause.message ?: "Unknown error",
            )
        }
    }
}
