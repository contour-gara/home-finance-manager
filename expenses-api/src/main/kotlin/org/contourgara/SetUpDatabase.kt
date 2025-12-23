package org.contourgara

import io.ktor.server.application.Application
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.v1.jdbc.Database

fun Application.setUpDatabase(appConfig: AppConfig) {
    Flyway
        .configure()
        .dataSource(
            appConfig.datasourceUrl,
            appConfig.datasourceUser,
            appConfig.datasourcePassword,
        )
        .driver("com.mysql.cj.jdbc.Driver")
        .load()
        .migrate()

    Database.connect(
        url = appConfig.datasourceUrl,
        driver = "com.mysql.cj.jdbc.Driver",
        user = appConfig.datasourceUser,
        password = appConfig.datasourcePassword,
    )
}
