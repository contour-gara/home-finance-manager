package org.contourgara.presentation

import io.ktor.server.application.Application
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import org.contourgara.application.nextUlid
import ulid.ULID

fun Application.configureRouting(findLatestUlid: () -> ULID, generateNextUlid: (ULID) -> ULID, saveUlid: (ULID) -> Unit) {
    routing {
        get("/health") {
            call.respondText("Hello World!")
        }

        get("/next-ulid") {
            call.respondText(
                nextUlid(
                    findLatestUlid = findLatestUlid,
                    generateNextUlid = generateNextUlid,
                    saveUlid = saveUlid,
                ).toString()
            )
        }
    }
}
