package org.contourgara

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.netty.EngineMain
import io.ktor.server.plugins.calllogging.CallLogging
import org.contourgara.generator.generateNextUlid
import org.contourgara.presentation.configureRouting
import org.contourgara.repository.UlidSequenceRepository
import org.contourgara.repository.migration
import org.slf4j.event.Level

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {
    install(plugin = CallLogging) {
        level = Level.DEBUG
    }
    configureRouting(
        findLatestUlid = { UlidSequenceRepository.findLatestUlid() },
        generateNextUlid = { ulid -> generateNextUlid(previous = ulid) },
        saveUlid = { ulid -> UlidSequenceRepository.insert(ulid) },
    )
    migration()
}
