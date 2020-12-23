package com.tm.rankme.storage.read.player

import com.tm.rankme.storage.read.MessageConsumer
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import java.util.*
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test

internal class PlayerCreatedConsumerTest {
    private val playerAccessor: MongoPlayerAccessor = mockk()
    private val consumer: MessageConsumer<PlayerCreatedMessage> = PlayerCreatedConsumer(playerAccessor)

    @Test
    internal fun `Should consume 'player-created' message`() {
        // given
        val message = PlayerCreatedMessage(UUID.randomUUID(), UUID.randomUUID(),"Optimus Prime", 187, 2428)
        val entity = PlayerEntity(message.aggregateId, message.leagueId, message.name, message.deviation, message.rating)
        every { playerAccessor.save(ofType(PlayerEntity::class)) } answers { entity }
        // when
        consumer.consume(message)
        // then
        val playerSlot = slot<PlayerEntity>()
        verify(exactly = 1) { playerAccessor.save(capture(playerSlot)) }
        assertEquals(message.aggregateId, playerSlot.captured.id)
        assertEquals(message.leagueId, playerSlot.captured.leagueId)
        assertEquals(message.name, playerSlot.captured.name)
        assertEquals(message.deviation, playerSlot.captured.deviation)
        assertEquals(message.rating, playerSlot.captured.rating)
    }
}