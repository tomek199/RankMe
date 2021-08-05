package com.tm.rankme.projection

import com.aventrix.jnanoid.jnanoid.NanoIdUtils.randomNanoId
import com.tm.rankme.model.player.Player
import com.tm.rankme.model.player.PlayerRepository
import io.mockk.*
import org.junit.jupiter.api.Test
import java.util.function.Consumer
import kotlin.test.assertEquals

internal class PlayerCreatedConsumerTest {
    private val repository: PlayerRepository = mockk()
    private val consumer: Consumer<PlayerCreatedMessage> = PlayerCreatedConsumer(repository)

    @Test
    internal fun `Should consume 'player-created' message`() {
        // given
        val message = PlayerCreatedMessage(randomNanoId(), randomNanoId(),"Optimus Prime", 187, 2428)
        every { repository.store(ofType(Player::class)) } just Runs
        // when
        consumer.accept(message)
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