package com.tm.rankme.projection

import com.tm.rankme.model.league.League
import com.tm.rankme.model.league.LeagueRepository
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import java.util.*
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test

internal class LeagueCreatedConsumerTest {
    private val repository: LeagueRepository = mockk()
    private val consumer: MessageConsumer<LeagueCreatedMessage> = LeagueCreatedConsumer(repository)

    @Test
    internal fun `Should consume 'league-created' message`() {
        // given
        val message = LeagueCreatedMessage(UUID.randomUUID(), "Star Wars", true, 3)
        every { repository.store(ofType(League::class)) } just Runs
        // when
        consumer.consume(message)
        // then
        val leagueSlot = slot<League>()
        verify(exactly = 1) { repository.store(capture(leagueSlot)) }
        assertEquals(message.aggregateId, leagueSlot.captured.id)
        assertEquals(message.name, leagueSlot.captured.name)
        assertEquals(message.allowDraws, leagueSlot.captured.allowDraws)
        assertEquals(message.maxScore, leagueSlot.captured.maxScore)
    }
}