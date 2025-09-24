package org.contourgara

import io.ktor.server.application.Application
import org.contourgara.presentation.configureRouting
import org.contourgara.repository.migration

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureRouting()
    migration()
}
