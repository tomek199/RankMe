package com.tm.rankme.storage.write.league

import com.tm.rankme.domain.base.EventBus
import com.tm.rankme.domain.league.LeagueCreated
import com.tm.rankme.storage.write.InfrastructureException
import io.mockk.every
import io.mockk.mockk
import java.util.*
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test

internal class EventSourceLeagueRepositoryTest {
    private val eventStorage = mockk<LeagueEventStorage>()
    private val eventEmitter = mockk<EventBus>()
    private val repository = EventSourceLeagueRepository(eventStorage, eventEmitter)

    @Test
    internal fun `Should return true when stream exist`() {
        // given
        val leagueId = UUID.randomUUID()
        every { eventStorage.events(leagueId.toString()) } returns listOf(LeagueCreated("Transformers"))
        // then
        assertTrue(repository.exist(leagueId))
    }

    @Test
    internal fun `Should return false when stream does not exist`() {
        // given
        val leagueId = UUID.randomUUID()
        every { eventStorage.events(leagueId.toString()) } throws InfrastructureException("Stream is not found")
        // then
        assertFalse(repository.exist(leagueId))
    }

    @Test
    internal fun `Should return false when stream does not contain any event`() {
        // given
        val leagueId = UUID.randomUUID()
        every { eventStorage.events(leagueId.toString()) } returns emptyList()
        // then
        assertFalse(repository.exist(leagueId))
    }
}