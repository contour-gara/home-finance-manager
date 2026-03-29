package org.contourgara.presentation

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.util.logging.error
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
