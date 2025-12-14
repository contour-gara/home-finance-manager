package org.contourgara

import org.flywaydb.core.Flyway

fun migration() {
    Flyway
        .configure()
        .dataSource(
            System.getenv("EXPENSES_API_DATASOURCE_URL"),
            System.getenv("DATASOURCE_USERNAME"),
            System.getenv("DATASOURCE_PASSWORD"),
        )
        .driver("com.mysql.cj.jdbc.Driver")
        .load()
        .migrate()
}
