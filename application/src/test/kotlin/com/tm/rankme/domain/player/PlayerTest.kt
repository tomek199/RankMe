package com.tm.rankme.domain.player

import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test

internal class PlayerTest {

    @Test
    internal fun `Should create league`() {
        // given
        val leagueId = UUID.randomUUID()
        val name = "Han Solo"
        // when
        val player = Player.create(leagueId, name)
        // then
        assertNotNull(player.id)
        assertEquals(leagueId, player.leagueId)
        assertEquals(0, player.version)
        assertEquals(name, player.name)
        assertEquals(350, player.deviation)
        assertEquals(1500, player.rating)
        assertEquals(1, player.pendingEvents.size)
        assertTrue(player.pendingEvents[0] is PlayerCreated)
        assertEquals(0, player.pendingEvents[0].version)
    }

    @Test
    internal fun `Should init player aggregate from 'player-created' event`() {
        // given
        val event = PlayerCreated(UUID.randomUUID(), "Han Solo")
        // when
        val player = Player.from(listOf(event))
        // then
        assertEquals(event.aggregateId, player.id)
        assertEquals(event.version, player.version)
        assertEquals(event.leagueId, player.leagueId)
        assertEquals(event.name, player.name)
        assertEquals(event.deviation, player.deviation)
        assertEquals(event.rating, player.rating)
        assertTrue(player.pendingEvents.isEmpty())
    }
}