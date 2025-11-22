package org.contourgara

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.calllogging.CallLogging
import org.contourgara.presentation.configureRouting
import org.contourgara.repository.migration
import org.slf4j.event.Level

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    install(CallLogging) {
        level = Level.DEBUG
    }
    configureRouting()
    migration()
}
