package com.tm.rankme.projection

import com.tm.rankme.model.league.League
import com.tm.rankme.model.league.LeagueRepository
import io.mockk.*
import org.junit.jupiter.api.Test
import java.util.*
import java.util.function.Consumer
import kotlin.test.assertEquals

internal class LeagueRenamedConsumerTest {
    private val repository: LeagueRepository = mockk()
    private val consumer: Consumer<LeagueRenamedMessage> = LeagueRenamedConsumer(repository)

    @Test
    internal fun `Should consume 'league-renamed' message and update league`() {
        // given
        val aggregateId = UUID.randomUUID()
        val league = League(aggregateId, "Start Wars", false, 7)
        every { repository.byId(aggregateId) } returns league
        every { repository.store(ofType(League::class)) } just Runs
        val message = LeagueRenamedMessage(aggregateId, "Transformers")
        // when
        consumer.accept(message)
        // then
        val leagueSlot = slot<League>()
        verifySequence {
            repository.byId(aggregateId)
            repository.store(capture(leagueSlot))
        }
        assertEquals(league.id, leagueSlot.captured.id)
        assertEquals(message.name, leagueSlot.captured.name)
        assertEquals(league.allowDraws, leagueSlot.captured.allowDraws)
        assertEquals(league.maxScore, leagueSlot.captured.maxScore)
    }

    @Test
    internal fun `Should consume 'league-renamed' message and do nothing when league is not found`() {
        // given
        val aggregateId = UUID.randomUUID()
        val message = LeagueRenamedMessage(aggregateId, "Transformers")
        every { repository.byId(aggregateId) } returns null
        // when
        consumer.accept(message)
        // then
        verify(exactly = 1) { repository.byId(aggregateId) }
        verify(exactly = 0) { repository.store(ofType(League::class)) }
    }
}