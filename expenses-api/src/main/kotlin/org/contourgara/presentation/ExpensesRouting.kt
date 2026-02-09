package org.contourgara.presentation

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import org.contourgara.application.MonthlyExpensesQueryService

fun Application.configureExpensesRouting(
    monthlyExpensesQueryService: MonthlyExpensesQueryService,
) {
    routing {
        route(path = "/expenses") {
            get(path = "{year}/{month}") {
                call
                    .parameters
                    .let {
                        Triple(
                            first = it["year"]!!.toInt(),
                            second = it["month"]!!.toInt(),
                            third = it["payer"],
                        )
                    }
                    .let { (year, month, payer) ->
                        monthlyExpensesQueryService.execute(year = year, month = month, payer = payer)
                    }
                    .let {
                        call.respond(
                            status = HttpStatusCode.OK,
                            message = it,
                        )
                    }
            }
        }
    }
}
