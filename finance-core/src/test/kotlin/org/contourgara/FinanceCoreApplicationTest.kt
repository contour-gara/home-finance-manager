package org.contourgara

import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.nulls.shouldNotBeNull
import org.springframework.test.context.ContextConfiguration

@ApplyExtension(SpringExtension::class)
@ContextConfiguration(classes = [FinanceCoreApplication::class])
class FinanceCoreApplicationTest(testComponent: TestComponent) : FunSpec({
    test("context loads") {
        testComponent.shouldNotBeNull()
    }
})
