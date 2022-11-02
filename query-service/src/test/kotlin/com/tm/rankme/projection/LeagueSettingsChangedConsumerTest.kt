package com.tm.rankme.projection

import com.aventrix.jnanoid.jnanoid.NanoIdUtils.randomNanoId
import com.tm.rankme.model.ModelChangeNotifier
import com.tm.rankme.model.league.League
import com.tm.rankme.model.league.LeagueRepository
import io.mockk.*
import org.junit.jupiter.api.Test
import java.util.function.Consumer
import kotlin.test.assertEquals

internal class LeagueSettingsChangedConsumerTest {
    private val repository: LeagueRepository = mockk()
    private val notifier: ModelChangeNotifier = mockk()
    private val consumer: Consumer<LeagueSettingsChangedMessage> = LeagueSettingsChangedConsumer(repository, notifier)

    @Test
    internal fun `Should consume 'league-setting-changed' message and update league`() {
        // given
        val aggregateId = randomNanoId()
        val league = League(aggregateId, "Start Wars", false, 2)
        every { repository.byId(aggregateId) } returns league
        every { repository.store(ofType(League::class)) } just Runs
        every { notifier.notify("league-settings-changed", ofType(League::class)) } just Runs
        val message = LeagueSettingsChangedMessage(aggregateId, true, 5)
        // when
        consumer.accept(message)
        // then
        val updatedLeagueSlot = slot<League>()
        val notificationLeagueSlot = slot<League>()
        verifySequence {
            repository.byId(aggregateId)
            repository.store(capture(updatedLeagueSlot))
            notifier.notify("league-settings-changed", capture(notificationLeagueSlot))
        }
        assertEquals(updatedLeagueSlot.captured, notificationLeagueSlot.captured)
        assertEquals(league.id, updatedLeagueSlot.captured.id)
        assertEquals(league.name, updatedLeagueSlot.captured.name)
        assertEquals(message.allowDraws, updatedLeagueSlot.captured.allowDraws)
        assertEquals(message.maxScore, updatedLeagueSlot.captured.maxScore)
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
        verify(exactly = 0) { notifier.notify(any(), ofType(League::class)) }
    }
}