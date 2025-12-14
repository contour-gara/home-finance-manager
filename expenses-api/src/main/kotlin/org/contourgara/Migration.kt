package org.contourgara

import org.flywaydb.core.Flyway

fun migration() {
    Flyway
        .configure()
        .dataSource(
            AppConfig.datasourceUrl,
            AppConfig.datasourceUser,
            AppConfig.datasourcePassword,
        )
        .driver("com.mysql.cj.jdbc.Driver")
        .load()
        .migrate()
}
