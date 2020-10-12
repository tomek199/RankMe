package com.tm.rankme.application.game

import com.tm.rankme.application.common.Mapper
import com.tm.rankme.domain.competitor.Competitor
import com.tm.rankme.domain.game.Game
import com.tm.rankme.domain.game.GameFactory
import com.tm.rankme.domain.game.Player
import com.tm.rankme.domain.game.Result
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import org.junit.jupiter.api.Test

internal class GameMapperTest {
    private val mapper: Mapper<Game, GameModel> = GameMapper()
    private val leagueId = "league-1"

    @Test
    internal fun `Should map game domain to model`() {
        // given
        val id = "game-1"
        val dateTime = LocalDateTime.now()
        val playerTwo = Player("comp-2", "Batman", 196, 2578, Result(2, -5, 96))
        val playerOne = Player("comp-1", "Superman", 258, 1345, Result(0, -8, -96))
        val domain = Game(id, playerOne, playerTwo, leagueId, dateTime)
        // when
        val model = mapper.toModel(domain)
        // then
        assertEquals(id, model.id)
        assertEquals(dateTime, model.dateTime)
        assertEquals(playerOne.competitorId, model.playerOne.competitorId)
        assertEquals(playerOne.username, model.playerOne.username)
        assertEquals(playerOne.rating, model.playerOne.rating)
        assertEquals(playerOne.result!!.score, model.playerOne.score)
        assertEquals(playerTwo.competitorId, model.playerTwo.competitorId)
        assertEquals(playerTwo.username, model.playerTwo.username)
        assertEquals(playerTwo.rating, model.playerTwo.rating)
        assertEquals(playerTwo.result!!.score, model.playerTwo.score)
    }

    @Test
    internal fun `Should throw IllegalStateException when domain game id is null`() {
        // given
        val competitorOne = Competitor(leagueId, "comp-1", "Batman")
        val competitorTwo = Competitor(leagueId, "comp-2", "Superman")
        val domain = GameFactory.completed(competitorOne, 2, competitorTwo, 3)
        // when
        val exception = assertFailsWith<IllegalStateException> { mapper.toModel(domain) }
        // then
        assertEquals("Game id can't be null!", exception.message)
    }
}
