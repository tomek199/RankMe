package com.tm.rankme.projection

import com.tm.rankme.infrastructure.LeagueEntity
import com.tm.rankme.infrastructure.MongoLeagueAccessor
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import java.util.*
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.data.repository.findByIdOrNull

internal class LeagueRenamedConsumerTest {
    private val leagueAccessor: MongoLeagueAccessor = mockk()
    private val consumer: MessageConsumer<LeagueRenamedMessage> = LeagueRenamedConsumer(leagueAccessor)

    @Test
    internal fun `Should consume 'league-renamed' message and update league`() {
        // given
        val aggregateId = UUID.randomUUID()
        val entity = LeagueEntity(aggregateId, "Start Wars", false, 7)
        every { leagueAccessor.findByIdOrNull(aggregateId) } returns entity
        every { leagueAccessor.save(ofType(LeagueEntity::class)) } answers { entity }
        val message = LeagueRenamedMessage(aggregateId, "Transformers")
        // when
        consumer.consume(message)
        // then
        val leagueSlot = slot<LeagueEntity>()
        verify(exactly = 1) { leagueAccessor.findByIdOrNull(aggregateId) }
        verify(exactly = 1) { leagueAccessor.save(capture(leagueSlot)) }
        assertEquals(entity.id, leagueSlot.captured.id)
        assertEquals(message.name, leagueSlot.captured.name)
        assertEquals(entity.allowDraws, leagueSlot.captured.allowDraws)
        assertEquals(entity.maxScore, leagueSlot.captured.maxScore)
    }

    @Test
    internal fun `Should consume 'league-renamed' message and do nothing when league is not found`() {
        // given
        val aggregateId = UUID.randomUUID()
        val message = LeagueRenamedMessage(aggregateId, "Transformers")
        every { leagueAccessor.findByIdOrNull(aggregateId) } returns null
        // when
        consumer.consume(message)
        // then
        verify(exactly = 1) { leagueAccessor.findByIdOrNull(aggregateId) }
        verify(exactly = 0) { leagueAccessor.save(ofType(LeagueEntity::class)) }
    }
}