package org.contourgara.domain

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.comparables.shouldBeEqualComparingTo
import io.kotest.matchers.comparables.shouldBeLessThan
import ulid.ULID

class ExpenseEventIdTest : FunSpec({
    test("ULID で比較できる") {
        // setup
        val before = ExpenseEventId(id = ULID.parseULID("01K4MXEKC0PMTJ8FA055N4SH79"))
        val after = ExpenseEventId(id = ULID.parseULID("01KD27JEZQQY88RG18034YZHBV"))

        // execute & assert
        before shouldBeLessThan after
    }

    test("ULID が同じ場合、等しい") {
        // setup
        val before = ExpenseEventId(id = ULID.parseULID("01K4MXEKC0PMTJ8FA055N4SH79"))
        val after = ExpenseEventId(id = ULID.parseULID("01K4MXEKC0PMTJ8FA055N4SH79"))

        // execute & assert
        before shouldBeEqualComparingTo after
    }
})
