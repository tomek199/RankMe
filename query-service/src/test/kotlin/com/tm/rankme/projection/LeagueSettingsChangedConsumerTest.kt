package com.tm.rankme.projection

import com.aventrix.jnanoid.jnanoid.NanoIdUtils.randomNanoId
import com.tm.rankme.model.league.League
import com.tm.rankme.model.league.LeagueRepository
import io.mockk.*
import org.junit.jupiter.api.Test
import java.util.function.Consumer
import kotlin.test.assertEquals

internal class LeagueSettingsChangedConsumerTest {
    private val repository: LeagueRepository = mockk()
    private val consumer: Consumer<LeagueSettingsChangedMessage> = LeagueSettingsChangedConsumer(repository)

    @Test
    internal fun `Should consume 'league-setting-changed' message and update league`() {
        // given
        val aggregateId = randomNanoId()
        val league = League(aggregateId, "Start Wars", false, 2)
        every { repository.byId(aggregateId) } returns league
        every { repository.store(ofType(League::class)) } just Runs
        val message = LeagueSettingsChangedMessage(aggregateId, true, 5)
        // when
        consumer.accept(message)
        // then
        val leagueSlot = slot<League>()
        verifySequence {
            repository.byId(aggregateId)
            repository.store(capture(leagueSlot))
        }
        assertEquals(league.id, leagueSlot.captured.id)
        assertEquals(league.name, leagueSlot.captured.name)
        assertEquals(message.allowDraws, leagueSlot.captured.allowDraws)
        assertEquals(message.maxScore, leagueSlot.captured.maxScore)
    }

    @Test
    internal fun `Should consume 'league-setting-changed' message and do nothing when league is not found`() {
        // given
        val aggregateId = randomNanoId()
        val message = LeagueSettingsChangedMessage(aggregateId, true, 5)
        every { repository.byId(aggregateId) } returns null
        // when
        consumer.accept(message)
        // then
        verify(exactly = 1) { repository.byId(aggregateId) }
        verify(exactly = 0) { repository.store(ofType(League::class)) }
    }
}