package org.contourgara.presentation

import io.ktor.server.application.Application
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import org.contourgara.application.nextUlid
import org.contourgara.repository.UlidSequenceRepository

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        get("/next-ulid") {
            call.respondText(
                nextUlid(
                    { UlidSequenceRepository.findLatestUlid() },
                    { ulid -> UlidSequenceRepository.insert(ulid) }
                ).toString()
            )
        }
    }
}
