package com.tm.rankme.storage.read.league

import com.tm.rankme.storage.read.MessageConsumer
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import java.util.*
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.data.repository.findByIdOrNull

internal class LeagueSettingsChangedConsumerTest {
    private val leagueAccessor: MongoLeagueAccessor = mockk()
    private val consumer: MessageConsumer<LeagueSettingsChangedMessage> = LeagueSettingsChangedConsumer(leagueAccessor)

    @Test
    internal fun `Should consume 'league-setting-changed' message and update league`() {
        // given
        val aggregateId = UUID.randomUUID()
        val entity = LeagueEntity(aggregateId, "Start Wars", false, 2)
        every { leagueAccessor.findByIdOrNull(aggregateId) } returns entity
        every { leagueAccessor.save(ofType(LeagueEntity::class)) } answers { entity }
        val message = LeagueSettingsChangedMessage(aggregateId, true, 5)
        // when
        consumer.consume(message)
        // then
        val leagueSlot = slot<LeagueEntity>()
        verify(exactly = 1) { leagueAccessor.findByIdOrNull(aggregateId) }
        verify(exactly = 1) { leagueAccessor.save(capture(leagueSlot)) }
        assertEquals(entity.id, leagueSlot.captured.id)
        assertEquals(entity.name, leagueSlot.captured.name)
        assertEquals(message.allowDraws, leagueSlot.captured.allowDraws)
        assertEquals(message.maxScore, leagueSlot.captured.maxScore)
    }

    @Test
    internal fun `Should consume 'league-setting-changed' message and do nothing when league is not found`() {
        // given
        val aggregateId = UUID.randomUUID()
        val message = LeagueSettingsChangedMessage(aggregateId, true, 5)
        every { leagueAccessor.findByIdOrNull(aggregateId) } returns null
        // when
        consumer.consume(message)
        // then
        verify(exactly = 1) { leagueAccessor.findByIdOrNull(aggregateId) }
        verify(exactly = 0) { leagueAccessor.save(ofType(LeagueEntity::class)) }
    }
}