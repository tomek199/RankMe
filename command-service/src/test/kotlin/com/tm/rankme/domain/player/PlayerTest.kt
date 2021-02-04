package com.tm.rankme.domain.player

import java.time.LocalDate
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

    @Test
    internal fun `Should play first game`() {
        // given
        val playerOne = Player.from(listOf(PlayerCreated(UUID.randomUUID(), "Batman")))
        val playerTwo = Player.from(listOf(PlayerCreated(UUID.randomUUID(), "Superman")))
        // when
        val gameResult = playerOne.playedWith(playerTwo, 5, 3)
        // then
        assertEquals(5, gameResult.firstScore)
        assertEquals(-60, gameResult.firstDeviationDelta)
        assertEquals(162, gameResult.firstRatingDelta)
        assertEquals(3, gameResult.secondScore)
        assertEquals(-60, gameResult.secondDeviationDelta)
        assertEquals(-162, gameResult.secondRatingDelta)

        assertEquals(LocalDate.now(), playerOne.lastGame)
        assertEquals(290, playerOne.deviation)
        assertEquals(1662, playerOne.rating)
        (playerOne.pendingEvents[0] as PlayerPlayedGame).let {
            assertEquals("player-played-game", it.type)
            assertEquals(-60, it.deviationDelta)
            assertEquals(162, it.ratingDelta)
            assertEquals(5, it.score)
            assertEquals(1, it.version)
            assertEquals(playerOne.id, it.aggregateId)
        }

        assertEquals(LocalDate.now(), playerTwo.lastGame)
        assertEquals(290, playerTwo.deviation)
        assertEquals(1338, playerTwo.rating)
        (playerTwo.pendingEvents[0] as PlayerPlayedGame).let {
            assertEquals("player-played-game", it.type)
            assertEquals(-60, it.deviationDelta)
            assertEquals(-162, it.ratingDelta)
            assertEquals(3, it.score)
            assertEquals(1, it.version)
            assertEquals(playerTwo.id, it.aggregateId)
        }
    }

    @Test
    internal fun `Should play second game`() {
        // given
        val playerOneCreated = PlayerCreated(UUID.randomUUID(), "Batman")
        val playerOnePlayed = PlayerPlayedGame(-35, 48, 4, playerOneCreated.aggregateId, 3)
        val playerOne = Player.from(listOf(playerOneCreated, playerOnePlayed))
        val playerTwoCreated = PlayerCreated(UUID.randomUUID(), "Superman")
        val playerTwoPlayed = PlayerPlayedGame(-56, -87, 2, playerTwoCreated.aggregateId, 4)
        val playerTwo = Player.from(listOf(playerTwoCreated, playerTwoPlayed))
        // when
        val gameResult = playerOne.playedWith(playerTwo, 7, 8)
        // then
        assertEquals(7, gameResult.firstScore)
        assertEquals(-46, gameResult.firstDeviationDelta)
        assertEquals(-192, gameResult.firstRatingDelta)
        assertEquals(8, gameResult.secondScore)
        assertEquals(-36, gameResult.secondDeviationDelta)
        assertEquals(170, gameResult.secondRatingDelta)

        assertEquals(LocalDate.now(), playerOne.lastGame)
        assertEquals(269, playerOne.deviation)
        assertEquals(1356, playerOne.rating)
        (playerOne.pendingEvents[0] as PlayerPlayedGame).let {
            assertEquals("player-played-game", it.type)
            assertEquals(-46, it.deviationDelta)
            assertEquals(-192, it.ratingDelta)
            assertEquals(7, it.score)
            assertEquals(4, it.version)
            assertEquals(playerOne.id, it.aggregateId)
        }

        assertEquals(LocalDate.now(), playerTwo.lastGame)
        assertEquals(258, playerTwo.deviation)
        assertEquals(1583, playerTwo.rating)
        (playerTwo.pendingEvents[0] as PlayerPlayedGame).let {
            assertEquals("player-played-game", it.type)
            assertEquals(-36, it.deviationDelta)
            assertEquals(170, it.ratingDelta)
            assertEquals(8, it.score)
            assertEquals(5, it.version)
            assertEquals(playerTwo.id, it.aggregateId)
        }
    }
}