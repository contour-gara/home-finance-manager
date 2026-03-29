package org.contourgara.application

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.contourgara.repository.UlidSequenceRepository
import ulid.ULID

class NextUlidUseCaseTest : FunSpec({
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
})
