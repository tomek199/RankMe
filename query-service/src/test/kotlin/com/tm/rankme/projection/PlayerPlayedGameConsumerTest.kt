package com.tm.rankme.projection

import com.aventrix.jnanoid.jnanoid.NanoIdUtils.randomNanoId
import com.tm.rankme.model.player.Player
import com.tm.rankme.model.player.PlayerRepository
import io.mockk.*
import org.junit.jupiter.api.Test
import java.util.function.Consumer
import kotlin.test.assertEquals

internal class PlayerPlayedGameConsumerTest {
    private val repository: PlayerRepository = mockk()
    private val consumer: Consumer<PlayerPlayedGameMessage> = PlayerPlayedGameConsumer(repository)

    @Test
    internal fun `Should consume 'player-played-game' message`() {
        // given
        val aggregateId = randomNanoId()
        val player = Player(aggregateId, randomNanoId(), "Optimus Prime", 241, 1665)
        every { repository.byId(aggregateId) } returns player
        every { repository.store(ofType(Player::class)) } just Runs
        val message = PlayerPlayedGameMessage(aggregateId, -53, 72)
        // when
        consumer.accept(message)
        // then
        val playerSlot = slot<Player>()
        verifySequence {
            repository.byId(aggregateId)
            repository.store(capture(playerSlot))
        }
        playerSlot.captured.let {
            assertEquals(player.id, it.id)
            assertEquals(player.leagueId, it.leagueId)
            assertEquals(player.name, it.name)
            assertEquals(188, it.deviation)
            assertEquals(1737, it.rating)
        }
    }

    @Test
    internal fun `Should consume 'player-played-game' and do nothing when player is not found`() {
        // given
        val aggregateId = randomNanoId()
        val message = PlayerPlayedGameMessage(aggregateId, -45, -72)
        every { repository.byId(aggregateId) } returns null
        // when
        consumer.accept(message)
        // then
        verify(exactly = 1) { repository.byId(aggregateId) }
        verify(exactly = 0) { repository.store(ofType(Player::class)) }
    }
}