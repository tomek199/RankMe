package com.tm.rankme.infrastructure.player

import com.aventrix.jnanoid.jnanoid.NanoIdUtils.randomNanoId
import com.tm.rankme.domain.base.AggregateException
import com.tm.rankme.domain.base.EventBus
import com.tm.rankme.domain.player.Player
import com.tm.rankme.domain.player.PlayerCreated
import com.tm.rankme.domain.player.PlayerPlayedGame
import com.tm.rankme.domain.player.PlayerRepository
import io.mockk.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

internal class PlayerAdapterTest {
    private val repository = mockk<PlayerRepository>()
    private val eventBus = mockk<EventBus>()
    private val adapter = PlayerAdapter(repository, eventBus)

    @Test
    internal fun `Should return Game`() {
        // given
        val leagueId = randomNanoId()
        val playerOne = Player.from(listOf(PlayerCreated(leagueId, "Batman")))
        val playerTwo = Player.from(listOf(PlayerCreated(leagueId, "Superman")))
        every { repository.byId(playerOne.id) } returns playerOne
        every { repository.byId(playerTwo.id) } returns playerTwo
        every { repository.store(ofType(Player::class))} just Runs
        every { eventBus.emit(ofType(PlayerPlayedGame::class)) } just Runs
        // when
        val game = adapter.playGame(playerOne.id, playerTwo.id, 3, 1)
        // then
        assertEquals(leagueId, game.leagueId)
        assertEquals(Pair(playerOne.id, playerTwo.id), game.playerIds)
        assertEquals(3, game.result!!.first.score)
        assertEquals(290, game.result!!.first.deviation)
        assertEquals(-60, game.result!!.first.deviationDelta)
        assertEquals(1662, game.result!!.first.rating)
        assertEquals(162, game.result!!.first.ratingDelta)
        assertEquals(1, game.result!!.second.score)
        assertEquals(290, game.result!!.second.deviation)
        assertEquals(-60, game.result!!.second.deviationDelta)
        assertEquals(1338, game.result!!.second.rating)
        assertEquals(-162, game.result!!.second.ratingDelta)
        verifySequence {
            repository.byId(playerOne.id)
            repository.byId(playerTwo.id)
            repository.store(ofType(Player::class))
            repository.store(ofType(Player::class))
            eventBus.emit(ofType(PlayerPlayedGame::class))
            eventBus.emit(ofType(PlayerPlayedGame::class))
        }
    }

    @Test
    internal fun `Should throw exception when players does not belong to the same league`() {
        // given
        val playerOne = Player.create(randomNanoId(), "Batman")
        val playerTwo = Player.create(randomNanoId(), "Superman")
        every { repository.byId(playerOne.id) } returns playerOne
        every { repository.byId(playerTwo.id) } returns playerTwo
        // when
        val exception = assertThrows<AggregateException> { adapter.playGame(playerOne.id, playerTwo.id, 2, 2) }
        // then
        assertEquals("Players ${playerOne.id} and ${playerTwo.id} does not belong to the same league", exception.message)
    }

    @Test
    internal fun `Should extract league id by player ids`() {
        // given
        val expectedLeagueId = randomNanoId()
        val playerOne = Player.create(expectedLeagueId, "Batman")
        val playerTwo = Player.create(expectedLeagueId, "Superman")
        every { repository.byId(playerOne.id) } returns playerOne
        every { repository.byId(playerTwo.id) } returns playerTwo
        // when
        val leagueId = adapter.extractLeagueId(playerOne.id, playerTwo.id)
        // then
        assertEquals(expectedLeagueId, leagueId)
    }
}