package org.contourgara.infrastructure.repository

import com.ninja_squad.dbsetup.destination.DriverManagerDestination
import com.ninja_squad.dbsetup_kotlin.dbSetup

object DbTestHelper {
    fun deleteAllData(url: String, user: String, password: String) {
        dbSetup(
            to = DriverManagerDestination(url, user, password),
        ) {
            deleteAllFrom(
                "expense_id",
                "expense_amount",
                "expense_payer",
                "expense_category",
                "expense_year",
                "expense_month",
                "expense_memo",
                "expense_event_id",
                "expense_event",
                "expense_event_category",
                "expenses_year",
                "expenses_month",
                "expenses_payer",
                "expenses_category",
                "expenses_amount",
            )
        }.launch()
    }
}
