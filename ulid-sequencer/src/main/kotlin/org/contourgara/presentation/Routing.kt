package org.contourgara.presentation

import io.ktor.server.application.Application
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import ulid.ULID

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        get("/next-ulid") {
            call.respondText(ULID.nextULID().toString())
        }
    }
}
