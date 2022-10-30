package com.tm.rankme.projection

import com.aventrix.jnanoid.jnanoid.NanoIdUtils.randomNanoId
import com.tm.rankme.model.ModelChangeNotifier
import com.tm.rankme.model.player.Player
import com.tm.rankme.model.player.PlayerRepository
import io.mockk.*
import org.junit.jupiter.api.Test
import java.util.function.Consumer
import kotlin.test.assertEquals

internal class PlayerCreatedConsumerTest {
    private val repository: PlayerRepository = mockk()
    private val notifier: ModelChangeNotifier = mockk()
    private val consumer: Consumer<PlayerCreatedMessage> = PlayerCreatedConsumer(repository, notifier)

    @Test
    internal fun `Should consume 'player-created' message`() {
        // given
        val message = PlayerCreatedMessage(randomNanoId(), randomNanoId(),"Optimus Prime", 187, 2428)
        every { repository.store(ofType(Player::class)) } just Runs
        every { notifier.notify("player-created", ofType(Player::class)) } just Runs
        // when
        consumer.accept(message)
        // then
        val createdPlayerSlot = slot<Player>()
        val notificationPlayerSlot = slot<Player>()
        verifySequence {
            repository.store(capture(createdPlayerSlot))
            notifier.notify("player-created", capture(notificationPlayerSlot))
        }
        assertEquals(createdPlayerSlot.captured, notificationPlayerSlot.captured)
        assertEquals(message.aggregateId, createdPlayerSlot.captured.id)
        assertEquals(message.leagueId, createdPlayerSlot.captured.leagueId)
        assertEquals(message.name, createdPlayerSlot.captured.name)
        assertEquals(message.deviation, createdPlayerSlot.captured.deviation)
        assertEquals(message.rating, createdPlayerSlot.captured.rating)
    }
}