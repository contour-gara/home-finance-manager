package org.contourgara

import org.flywaydb.core.Flyway

fun migration(appConfig: AppConfig) {
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
}
