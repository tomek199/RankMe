package com.tm.rankme.infrastructure

import com.aventrix.jnanoid.jnanoid.NanoIdUtils.randomNanoId
import com.tm.rankme.model.ModelChangeNotifier
import com.tm.rankme.model.player.Player
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.cloud.stream.function.StreamBridge
import org.springframework.messaging.support.GenericMessage
import kotlin.test.assertEquals

internal class SpringModelChangeNotifierTest {
    private val streamBridge = mockk<StreamBridge>()
    private val notifier: ModelChangeNotifier = SpringModelChangeNotifier(streamBridge)

    @Test
    internal fun `Should emit notification`() {
        // given
        val exchangeName = "notifications-out-0"
        val type = "player-created"
        val model = Player(randomNanoId(), randomNanoId(), "Optimus Prime", 350, 1500)
        every { streamBridge.send(exchangeName, ofType(GenericMessage::class)) } returns true
        // when
        notifier.notify(type, model)
        // then
        val messageSlot = slot<GenericMessage<Player>>()
        verify(exactly = 1) { streamBridge.send(exchangeName, capture(messageSlot))}
        assertEquals("query-$type", messageSlot.captured.headers["type"])
    }
}
