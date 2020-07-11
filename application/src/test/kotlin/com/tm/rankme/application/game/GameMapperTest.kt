package com.tm.rankme.application.game

import com.tm.rankme.application.common.Mapper
import com.tm.rankme.domain.competitor.Competitor
import com.tm.rankme.domain.competitor.Statistics
import com.tm.rankme.domain.game.Game
import com.tm.rankme.domain.game.GameFactory
import com.tm.rankme.domain.game.Player
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class GameMapperTest {
    private val mapper: Mapper<Game, GameModel> = GameMapper()
    private val leagueId = "league-1"

    @Test
    internal fun `Should map completed game domain to model`() {
        // given
        val id = "game-1"
        val dateTime = LocalDateTime.now()
        val playerTwo = Player("comp-2", "Batman", 196, 2578)
        val playerOne = Player("comp-1", "Superman", 258, 1345)
        val domain = Game(id, playerOne, playerTwo, leagueId, dateTime)
        // when
        val model = mapper.toModel(domain)
        // then
        assertEquals(id, model.id)
        assertEquals(dateTime, model.dateTime)
        assertEquals(playerOne.competitorId, model.playerOne.competitorId)
        assertEquals(playerOne.username, model.playerOne.username)
        assertEquals(playerOne.rating, model.playerOne.rating)
        assertEquals(playerOne.score, model.playerOne.score)
        assertEquals(playerTwo.competitorId, model.playerTwo.competitorId)
        assertEquals(playerTwo.username, model.playerTwo.username)
        assertEquals(playerTwo.rating, model.playerTwo.rating)
        assertEquals(playerTwo.score, model.playerTwo.score)
    }

    @Test
    internal fun `Should throw IllegalStateException when domain game id is null`() {
        // given
        val competitorOne = Competitor(leagueId, "comp-1", "Batman", Statistics())
        val competitorTwo = Competitor(leagueId, "comp-2", "Superman", Statistics())
        val domain = GameFactory.create(competitorOne, 2, competitorTwo, 3, leagueId)
        // when
        val exception = assertFailsWith<IllegalStateException> { mapper.toModel(domain) }
        // then
        assertEquals("Game id can't be null!", exception.message)
    }
}
