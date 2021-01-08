package com.tm.rankme.storage.write.player

import com.tm.rankme.domain.base.AggregateException
import com.tm.rankme.domain.player.Player
import com.tm.rankme.domain.player.PlayerRepository
import io.mockk.every
import io.mockk.mockk
import java.util.*
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class PlayerAdapterTest {
    private val repository = mockk<PlayerRepository>()
    private val adapter = PlayerAdapter(repository)

    @Test
    internal fun `Should return Game`() {
        // given
        val leagueId = UUID.randomUUID()
        val playerOne = Player.create(leagueId, "Batman")
        val playerTwo = Player.create(leagueId, "Superman")
        every { repository.byId(playerOne.id) } returns playerOne
        every { repository.byId(playerTwo.id) } returns playerTwo
        // when
        val game = adapter.playGame(playerOne.id, playerTwo.id, 3, 1)
        // then
        assertEquals(leagueId, game.leagueId)
        assertEquals(Pair(playerOne.id, playerTwo.id), game.playerIds)
        assertEquals(3, game.result!!.first.score)
        assertEquals(-60, game.result!!.first.deviationDelta)
        assertEquals(162, game.result!!.first.ratingDelta)
        assertEquals(1, game.result!!.second.score)
        assertEquals(-60, game.result!!.second.deviationDelta)
        assertEquals(-162, game.result!!.second.ratingDelta)
    }

    @Test
    internal fun `Should throw exception when players does not belong to the same league`() {
        // given
        val playerOne = Player.create(UUID.randomUUID(), "Batman")
        val playerTwo = Player.create(UUID.randomUUID(), "Superman")
        every { repository.byId(playerOne.id) } returns playerOne
        every { repository.byId(playerTwo.id) } returns playerTwo
        // when
        val exception = assertThrows<AggregateException> { adapter.playGame(playerOne.id, playerTwo.id, 2, 2) }
        // then
        assertEquals("Players ${playerOne.id} and ${playerTwo.id} does not belong to the same league", exception.message)
    }
}