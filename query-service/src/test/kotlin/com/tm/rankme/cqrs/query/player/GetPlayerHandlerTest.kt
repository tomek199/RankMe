package com.tm.rankme.cqrs.query.player

import com.tm.rankme.cqrs.query.QueryHandler
import com.tm.rankme.model.player.Player
import com.tm.rankme.model.player.PlayerRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import org.junit.jupiter.api.Test

internal class GetPlayerHandlerTest {
    private val repository = mockk<PlayerRepository>()
    private val handler: QueryHandler<GetPlayerQuery, Player?> = GetPlayerHandler(repository)

    @Test
    internal fun `Should return player`() {
        // given
        val query = GetPlayerQuery(UUID.randomUUID())
        val expectedPlayer = Player(query.id, "Optimus Prime", 321, 1686)
        every { repository.byId(query.id) } returns expectedPlayer
        // when
        val player = handler.handle(query)
        // then
        verify(exactly = 1) { repository.byId(query.id) }
        assertNotNull(player)
        assertEquals(query.id, player.id)
        assertEquals(expectedPlayer.name, player.name)
        assertEquals(expectedPlayer.deviation, player.deviation)
        assertEquals(expectedPlayer.rating, player.rating)
    }

    @Test
    internal fun `Should return null when player does not exist`() {
        // given
        val query = GetPlayerQuery(UUID.randomUUID())
        every { repository.byId(query.id) } returns null
        // when
        val player = handler.handle(query)
        // then
        verify(exactly = 1) { repository.byId(query.id) }
        assertNull(player)
    }
}