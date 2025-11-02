package org.contourgara

import io.ktor.server.application.Application
import io.ktor.server.netty.EngineMain
import org.contourgara.presentation.configureRouting
import org.contourgara.repository.migration

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    configureRouting()
    migration()
}
