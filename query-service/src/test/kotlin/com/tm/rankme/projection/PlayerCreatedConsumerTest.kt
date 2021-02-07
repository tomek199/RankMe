package com.tm.rankme.projection

import com.tm.rankme.model.player.Player
import com.tm.rankme.model.player.PlayerRepository
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import java.util.*
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test

internal class PlayerCreatedConsumerTest {
    private val repository: PlayerRepository = mockk()
    private val consumer: MessageConsumer<PlayerCreatedMessage> = PlayerCreatedConsumer(repository)

    @Test
    internal fun `Should consume 'player-created' message`() {
        // given
        val message = PlayerCreatedMessage(UUID.randomUUID(), UUID.randomUUID(),"Optimus Prime", 187, 2428)
        every { repository.store(ofType(Player::class)) } just Runs
        // when
        consumer.consume(message)
        // then
        val playerSlot = slot<Player>()
        verify(exactly = 1) { repository.store(capture(playerSlot)) }
        assertEquals(message.aggregateId, playerSlot.captured.id)
        assertEquals(message.leagueId, playerSlot.captured.leagueId)
        assertEquals(message.name, playerSlot.captured.name)
        assertEquals(message.deviation, playerSlot.captured.deviation)
        assertEquals(message.rating, playerSlot.captured.rating)
    }
}