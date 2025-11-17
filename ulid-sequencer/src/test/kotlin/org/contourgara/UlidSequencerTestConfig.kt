package org.contourgara

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.spec.SpecExecutionOrder

object UlidSequencerTestConfig : AbstractProjectConfig() {
    override val specExecutionOrder = SpecExecutionOrder.Annotated
}
