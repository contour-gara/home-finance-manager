package org.contourgara

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import kotlinx.serialization.SerializationException

fun Application.configureRouting() {
    routing {
        route("/") {
            get {
                call.respondText { "Expenses API is running!" }
            }
        }

//        route("/expense") {
//            post {
//                try {
//                    val expense = call.receive<Expense>()
//                    call.respond(HttpStatusCode.Created, "イベント ID")
//                } catch (e: SerializationException) {
//                    call.respond(HttpStatusCode.BadRequest, e.message.toString())
//                }
//            }
//        }
    }
}
