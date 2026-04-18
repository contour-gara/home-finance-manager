package org.contourgara.domain

import kotlinx.datetime.LocalDate

interface SystemClock {
    fun today(): LocalDate
}
