package org.contourgara.application

import ulid.ULID

fun nextUlid(latestUlid: () -> ULID, saveUlid: (ULID) -> Unit): ULID {
    val ulid = ULID.Monotonic.nextULID(latestUlid())
    saveUlid(ulid)
    return ulid
}
