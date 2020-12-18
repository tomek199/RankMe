package com.tm.rankme.storage.read.league

import com.tm.rankme.storage.read.MessageConsumer
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import java.util.*
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test

internal class LeagueCreatedConsumerTest {
    private val leagueAccessor: MongoLeagueAccessor = mockk()
    private val consumer: MessageConsumer<LeagueCreatedMessage> = LeagueCreatedConsumer(leagueAccessor)

    @Test
    internal fun `Should consume message`() {
        // given
        val message = LeagueCreatedMessage(UUID.randomUUID(), "Star Wars", true, 3)
        val entity = LeagueEntity(message.aggregateId, message.name, message.allowDraws, message.maxScore)
        every { leagueAccessor.save(ofType(LeagueEntity::class)) } answers { entity }
        // when
        consumer.consume(message)
        // then
        val leagueSlot = slot<LeagueEntity>()
        verify(exactly = 1) { leagueAccessor.save(capture(leagueSlot)) }
        assertEquals(message.aggregateId, leagueSlot.captured.id)
        assertEquals(message.name, leagueSlot.captured.name)
        assertEquals(message.allowDraws, leagueSlot.captured.allowDraws)
        assertEquals(message.maxScore, leagueSlot.captured.maxScore)
    }
}