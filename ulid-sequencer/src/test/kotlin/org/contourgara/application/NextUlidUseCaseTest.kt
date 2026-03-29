package org.contourgara.application

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.contourgara.generator.UlidOverflowException
import org.contourgara.repository.UlidSequenceRepository
import org.jetbrains.exposed.v1.jdbc.Database
import ulid.ULID

class NextUlidUseCaseTest : FunSpec({
    beforeSpec {
        Database.connect(
            url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
            driver = "org.h2.Driver",
            user = "sa",
            password = "",
        )
    }

    test("渡された関数を使い、ULID を生成し返す") {
        // setup
        val initialUlid = ULID.parseULID(ulidString = "01K4MXEKC0PMTJ8FA055N4SH79")
        val nextUlid = ULID.parseULID(ulidString = "01K4MXEKC0PMTJ8FA055N4SH7A")

        val ulidSequenceRepository = mockk<UlidSequenceRepository>()
        every { ulidSequenceRepository.findLatestUlid() } returns initialUlid
        every { ulidSequenceRepository.insert(ulid = nextUlid) } returns Unit

        // execute
        val actual = nextUlid(
            findLatestUlid = { ulidSequenceRepository.findLatestUlid() },
            generateNextUlid = { nextUlid },
            saveUlid = { ulid -> ulidSequenceRepository.insert(ulid) },
        )

        // assert
        assertSoftly {
            actual shouldBeGreaterThan initialUlid
            verify(exactly = 1) { ulidSequenceRepository.insert(ulid = nextUlid) }
        }
    }

    test("ULID 生成でオーバーフローが発生した場合、ApplicationException を投げる") {
        // setup
        val initialUlid = ULID.parseULID(ulidString = "01K4MXEKC0PMTJ8FA055N4SH79")
        val nextUlid = ULID.parseULID(ulidString = "01K4MXEKC0PMTJ8FA055N4SH7A")

        val ulidSequenceRepository = mockk<UlidSequenceRepository>()
        every { ulidSequenceRepository.findLatestUlid() } returns initialUlid
        every { ulidSequenceRepository.insert(ulid = nextUlid) } returns Unit

        // execute & assert
        shouldThrow<ApplicationException> {
            nextUlid(
                findLatestUlid = { ulidSequenceRepository.findLatestUlid() },
                generateNextUlid = { throw UlidOverflowException() },
                saveUlid = { ulid -> ulidSequenceRepository.insert(ulid) },
            )
        }
            .cause shouldBe UlidOverflowException()
    }
})
