package com.tm.rankme.infrastructure.game

import com.tm.rankme.model.game.PlayerPort
import com.tm.rankme.model.player.Player
import com.tm.rankme.model.player.PlayerRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class PlayerAdapterTest {
    private val repository: PlayerRepository = mockk()
    private val port: PlayerPort = PlayerAdapter(repository)

    @Test
    internal fun `Should return player name`() {
        // given
        val leagueId = UUID.randomUUID()
        val player = Player(UUID.randomUUID(), leagueId, "Batman", 186, 2729)
        every { repository.byId(player.id) } returns player
        // when
        val playerName = port.playerName(player.id)
        // then
        assertEquals(player.name, playerName)
    }

    @Test
    internal fun `Should throw exception when player name is not found`() {
        // given
        val playerId = UUID.randomUUID()
        every { repository.byId(playerId) } returns null
        // when
        val exception = assertFailsWith<IllegalStateException> { port.playerName(playerId) }
        // then
        assertEquals("Player $playerId is not found", exception.message)
    }

    @Test
    internal fun `Should return player info`() {
        // given
        val leagueId = UUID.randomUUID()
        val player = Player(UUID.randomUUID(), leagueId, "Batman", 186, 2729)
        every { repository.byId(player.id) } returns player
        // when
        val playerInfo = port.playerInfo(player.id)
        // then
        assertEquals(player.name, playerInfo.name)
        assertEquals(player.deviation, playerInfo.deviation)
        assertEquals(player.rating, playerInfo.rating)
    }

    @Test
    internal fun `Should throw exception when player info is not found`() {
        // given
        val playerId = UUID.randomUUID()
        every { repository.byId(playerId) } returns null
        // when
        val exception = assertFailsWith<IllegalStateException> { port.playerInfo(playerId) }
        // then
        assertEquals("Player $playerId is not found", exception.message)
    }
}