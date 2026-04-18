package org.contourgara.infrastructure

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.contourgara.domain.SystemClock
import org.koin.core.annotation.Single
import kotlin.time.Clock

@Single
class SystemClockImpl : SystemClock {
    override fun today(): LocalDate =
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
}
