package com.tm.rankme.projection

import com.aventrix.jnanoid.jnanoid.NanoIdUtils.randomNanoId
import com.tm.rankme.model.ModelChangeNotifier
import com.tm.rankme.model.league.League
import com.tm.rankme.model.league.LeagueRepository
import io.mockk.*
import org.junit.jupiter.api.Test
import java.util.function.Consumer
import kotlin.test.assertEquals

internal class LeagueCreatedConsumerTest {
    private val repository: LeagueRepository = mockk()
    private val notifier: ModelChangeNotifier = mockk()
    private val consumer: Consumer<LeagueCreatedMessage> = LeagueCreatedConsumer(repository, notifier)

    @Test
    internal fun `Should consume 'league-created' message`() {
        // given
        val message = LeagueCreatedMessage(randomNanoId(), "Star Wars", true, 3)
        every { repository.store(ofType(League::class)) } just Runs
        every { notifier.notify("league-created", ofType(League::class)) } just Runs
        // when
        consumer.accept(message)
        // then
        val createdLeagueSlot = slot<League>()
        val notificationLeagueSlot = slot<League>()
        verifySequence {
            repository.store(capture(createdLeagueSlot))
            notifier.notify("league-created", capture(notificationLeagueSlot))
        }
        assertEquals(createdLeagueSlot.captured, notificationLeagueSlot.captured)
        assertEquals(message.aggregateId, createdLeagueSlot.captured.id)
        assertEquals(message.name, createdLeagueSlot.captured.name)
        assertEquals(message.allowDraws, createdLeagueSlot.captured.allowDraws)
        assertEquals(message.maxScore, createdLeagueSlot.captured.maxScore)
    }
}