package com.tm.rankme.projection

import com.aventrix.jnanoid.jnanoid.NanoIdUtils.randomNanoId
import com.tm.rankme.model.league.League
import com.tm.rankme.model.league.LeagueRepository
import io.mockk.*
import org.junit.jupiter.api.Test
import java.util.function.Consumer
import kotlin.test.assertEquals

internal class LeagueCreatedConsumerTest {
    private val repository: LeagueRepository = mockk()
    private val consumer: Consumer<LeagueCreatedMessage> = LeagueCreatedConsumer(repository)

    @Test
    internal fun `Should consume 'league-created' message`() {
        // given
        val message = LeagueCreatedMessage(randomNanoId(), "Star Wars", true, 3)
        every { repository.store(ofType(League::class)) } just Runs
        // when
        consumer.accept(message)
        // then
        val leagueSlot = slot<League>()
        verify(exactly = 1) { repository.store(capture(leagueSlot)) }
        assertEquals(message.aggregateId, leagueSlot.captured.id)
        assertEquals(message.name, leagueSlot.captured.name)
        assertEquals(message.allowDraws, leagueSlot.captured.allowDraws)
        assertEquals(message.maxScore, leagueSlot.captured.maxScore)
    }
}