package com.tm.rankme.storage.write.game

import com.tm.rankme.domain.base.EventEmitter
import io.mockk.mockk
import kotlin.test.assertNotNull
import org.junit.jupiter.api.Test

internal class EventSourceGameRepositoryTest {
    @Test
    internal fun `Should create repository`() {
        // given
        val eventEmitter = mockk<EventEmitter>()
        val eventStorage = mockk<GameEventStorage>()
        // when
        val repository = EventSourceGameRepository(eventStorage, eventEmitter)
        // then
        assertNotNull(repository)
    }

}