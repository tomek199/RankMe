package com.tm.rankme.storage.write.player

import com.tm.rankme.domain.base.EventEmitter
import io.mockk.mockk
import kotlin.test.assertNotNull
import org.junit.jupiter.api.Test

internal class EventSourcePlayerRepositoryTest {
    @Test
    internal fun `Should create repository`() {
        // given
        val eventEmitter = mockk<EventEmitter>()
        val eventStorage = mockk<PlayerEventStorage>()
        // when
        val repository = EventSourcePlayerRepository(eventStorage, eventEmitter)
        // then
        assertNotNull(repository)
    }
}