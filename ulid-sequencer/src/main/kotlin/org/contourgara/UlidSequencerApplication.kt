package org.contourgara

import io.ktor.server.application.Application
import org.contourgara.presentation.configureRouting

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureRouting()
}
