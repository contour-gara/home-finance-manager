package org.contourgara.domain

import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import ulid.ULID

class ExpenseEventTest : FunSpec({
    test("削除済みの支出を削除しようとした場合、ExpenseAlreadyDeletedError が返る") {
        // setup
        val expenseEvent = ExpenseEvent(
            expenseEventId = ExpenseEventId(value = ULID.parseULID("01KD27JEZQQY88RG18034YZHBV")),
            expenseId = ExpenseId(value = "01K4MXEKC0PMTJ8FA055N4SH79"),
            eventCategory = EventCategory.DELETE,
        )

        val deleteEventId = ExpenseEventId(value = ULID.parseULID("01KDHVD5XTTR9XR4ZAFSSETGXS"))

        // execute
        val actual = expenseEvent.delete(deleteEventId = deleteEventId)

        // assert
        val expected = ExpenseAlreadyDeletedError(
            expenseId = "01K4MXEKC0PMTJ8FA055N4SH79",
        )
        assertSoftly {
            actual.shouldBeLeft()
            actual.value shouldBe expected
        }
    }

    test("作成イベント ID よりも古い削除イベント ID で削除しようとした場合、IllegalArgumentException が投げれれる") {
        // setup
        val expenseEvent = ExpenseEvent(
            expenseEventId = ExpenseEventId(value = ULID.parseULID("01KDHVD5XTTR9XR4ZAFSSETGXS")),
            expenseId = ExpenseId(value = "01K4MXEKC0PMTJ8FA055N4SH79"),
            eventCategory = EventCategory.DELETE,
        )

        val deleteEventId = ExpenseEventId(value = ULID.parseULID("01KD27JEZQQY88RG18034YZHBV"))

        // execute & assert
        shouldThrowExactly<IllegalArgumentException> {
            expenseEvent.delete(deleteEventId = deleteEventId)
        }.message shouldBe "deleteEventId must be greater than expenseEventId: deleteEventId = 01KD27JEZQQY88RG18034YZHBV, expenseEventId = 01KDHVD5XTTR9XR4ZAFSSETGXS"
    }

    test("削除されていない支出を削除しようとした場合、削除済みの ExpenseEvent が返る") {
        // setup
        val expenseEvent = ExpenseEvent(
            expenseEventId = ExpenseEventId(value = ULID.parseULID("01KD27JEZQQY88RG18034YZHBV")),
            expenseId = ExpenseId(value = "01K4MXEKC0PMTJ8FA055N4SH79"),
            eventCategory = EventCategory.CREATE,
        )

        val deleteEventId = ExpenseEventId(value = ULID.parseULID("01KDHVD5XTTR9XR4ZAFSSETGXS"))

        // execute
        val actual = expenseEvent.delete(deleteEventId = deleteEventId)

        // assert
        val expected = ExpenseEvent(
            expenseEventId = deleteEventId,
            expenseId = ExpenseId(value = "01K4MXEKC0PMTJ8FA055N4SH79"),
            eventCategory = EventCategory.DELETE,
        )
        assertSoftly {
            actual.shouldBeRight()
            actual.value shouldBe expected
        }
    }
})
