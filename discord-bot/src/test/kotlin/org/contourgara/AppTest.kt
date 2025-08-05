package org.contourgara

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldNotBeNull

class AppTest : StringSpec({
    "appHasAGreeting" {
        val sut = App()
        sut.greeting shouldNotBeNull {}
    }
})
