package com.tm.rankme.storage.write.league

import com.tm.rankme.domain.base.EventEmitter
import io.mockk.mockk
import kotlin.test.assertNotNull
import org.junit.jupiter.api.Test

internal class EventSourceLeagueRepositoryTest {
    @Test
    internal fun `Should create repository`() {
        // given
        val eventEmitter = mockk<EventEmitter>()
        val eventStorage = mockk<LeagueEventStorage>()
        // when
        val repository = EventSourceLeagueRepository(eventStorage, eventEmitter)
        // then
        assertNotNull(repository)
    }
}