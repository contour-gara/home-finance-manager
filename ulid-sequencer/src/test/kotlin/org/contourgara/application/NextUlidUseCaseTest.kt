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
        val initUlid = ULID.parseULID("01K4MXEKC0PMTJ8FA055N4SH79")
        val ulidSequenceRepository = mockk<UlidSequenceRepository>()
        every { ulidSequenceRepository.findLatestUlid() } returns initUlid
        every { ulidSequenceRepository.insert(any()) } returns Unit

        // execute
        val actual = nextUlid(
            { ulidSequenceRepository.findLatestUlid() },
            { ulid -> ulidSequenceRepository.insert(ulid) },
        )

        // assert
        assertSoftly {
            actual shouldBeGreaterThan initUlid
            verify(exactly = 1) { ulidSequenceRepository.insert(any()) }
        }
    }
})
