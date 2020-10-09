package com.tm.rankme.application.game

import com.tm.rankme.application.any
import com.tm.rankme.application.common.Mapper
import com.tm.rankme.application.competitor.CompetitorService
import com.tm.rankme.application.match.MatchService
import com.tm.rankme.domain.Side
import com.tm.rankme.domain.competitor.Competitor
import com.tm.rankme.domain.game.Game
import com.tm.rankme.domain.game.GameFactory
import com.tm.rankme.domain.game.GameRepository
import com.tm.rankme.domain.game.Player
import com.tm.rankme.domain.game.Result
import com.tm.rankme.domain.match.Match
import com.tm.rankme.domain.match.Member
import graphql.relay.Connection
import graphql.schema.DataFetchingEnvironment
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import org.mockito.Mockito.only
import org.mockito.Mockito.times
import org.mockito.Mockito.verify

internal class GameServiceTest {
    private val gameRepository: GameRepository = mock(GameRepository::class.java)
    private val competitorService: CompetitorService = mock(CompetitorService::class.java)
    private val matchService: MatchService = mock(MatchService::class.java)
    private val mapper: Mapper<Game, GameModel> = GameMapper()

    private val service: GameService = GameServiceImpl(gameRepository, competitorService, matchService, mapper)

    private val gameId = "game-1"
    private val leagueId = "league-1"
    private val matchId = "match-1"
    private val playerOne = Player("comp-1", "Batman", 235, 1683, Result(3, -8,46))
    private val playerTwo = Player("comp-2", "Superman", 386, 2748, Result(1, -9,-46))

    @Test
    internal fun `Should return game`() {
        val expectedGame = Game(gameId, playerOne, playerTwo, leagueId, LocalDateTime.now())
        given(gameRepository.findById(gameId)).willReturn(expectedGame)
        // when
        val game: GameModel = service.get(gameId)
        // then
        assertEquals(expectedGame.id, game.id)
        assertEquals(expectedGame.playerOne.username, game.playerOne.username)
        assertEquals(expectedGame.playerOne.competitorId, game.playerOne.competitorId)
        assertEquals(expectedGame.playerTwo.username, game.playerTwo.username)
        assertEquals(expectedGame.playerTwo.competitorId, game.playerTwo.competitorId)
        assertEquals(expectedGame.dateTime, game.dateTime)
        verify(gameRepository, only()).findById(gameId)
    }

    @Test
    internal fun `Should throw IllegalStateException when game does not exist`() {
        // given
        given(gameRepository.findById(gameId)).willReturn(null)
        // when
        val exception = assertFailsWith<IllegalStateException> { service.get(gameId) }
        // then
        assertEquals("Game $gameId is not found", exception.message)
    }

    @Test
    internal fun `Should create new game`() {
        // given
        val lastGameDate = LocalDate.now()
        val firstCompetitor = Competitor(leagueId, "comp-1", "Batman", 285, 1868, lastGameDate)
        val secondCompetitor = Competitor(leagueId, "comp-2", "Superman", 182, 2593, lastGameDate)
        given(competitorService.getForLeague(firstCompetitor.id!!, leagueId)).willReturn(firstCompetitor)
        given(competitorService.getForLeague(secondCompetitor.id!!, leagueId)).willReturn(secondCompetitor)
        val expectedGame = Game(gameId, playerOne, playerTwo, leagueId, LocalDateTime.now())
        given(gameRepository.save(any(Game::class.java))).willReturn(expectedGame)
        // when
        val game: GameModel = service.create(
            leagueId, firstCompetitor.id!!, playerOne.result!!.score,
            secondCompetitor.id!!, playerTwo.result!!.score
        )
        // then
        verify(gameRepository, only()).save(any(Game::class.java))
        verify(competitorService, times(1)).getForLeague(firstCompetitor.id!!, leagueId)
        verify(competitorService, times(1)).getForLeague(secondCompetitor.id!!, leagueId)
        verify(competitorService, times(1))
            .updateStatistic(any(Competitor::class.java), any(Competitor::class.java), any(Game::class.java))
        assertEquals(firstCompetitor.id, game.playerOne.competitorId)
        assertEquals(firstCompetitor.username, playerOne.username)
        assertEquals(playerOne.rating, game.playerOne.rating)
        assertEquals(playerOne.result!!.score, game.playerOne.score)
        assertEquals(secondCompetitor.id, game.playerTwo.competitorId)
        assertEquals(secondCompetitor.username, playerTwo.username)
        assertEquals(playerTwo.rating, game.playerTwo.rating)
        assertEquals(playerTwo.result!!.score, game.playerTwo.score)
    }

    @Test
    internal fun `Should complete match and create game`() {
        // given
        val lastGameDate = LocalDate.now()
        val firstCompetitor = Competitor(leagueId, "comp-1", "Batman", 285, 1868, lastGameDate)
        val secondCompetitor = Competitor(leagueId, "comp-2", "Superman", 182, 2593, lastGameDate)
        given(competitorService.getForLeague(firstCompetitor.id!!, leagueId)).willReturn(firstCompetitor)
        given(competitorService.getForLeague(secondCompetitor.id!!, leagueId)).willReturn(secondCompetitor)
        val expectedGame = Game(gameId, playerOne, playerTwo, leagueId, LocalDateTime.now())
        given(gameRepository.save(any(Game::class.java))).willReturn(expectedGame)
        val memberOne = Member(
            firstCompetitor.id!!, firstCompetitor.username,
            firstCompetitor.deviation, firstCompetitor.rating
        )
        val memberTwo = Member(
            secondCompetitor.id!!, secondCompetitor.username,
            secondCompetitor.deviation, secondCompetitor.rating
        )
        given(matchService.getScheduled(matchId)).willReturn(Match(matchId, leagueId, memberOne, memberTwo, LocalDateTime.now()))
        // when
        val game: GameModel = service.complete(matchId, playerOne.result!!.score, playerTwo.result!!.score)
        // then
        verify(gameRepository, only()).save(any(Game::class.java))
        verify(competitorService, times(1)).getForLeague(firstCompetitor.id!!, leagueId)
        verify(competitorService, times(1)).getForLeague(secondCompetitor.id!!, leagueId)
        verify(competitorService, times(1))
            .updateStatistic(any(Competitor::class.java), any(Competitor::class.java), any(Game::class.java))
        verify(matchService, times(1)).getScheduled(matchId)
        verify(matchService, times(1)).complete(matchId, gameId)
        assertEquals(firstCompetitor.id, game.playerOne.competitorId)
        assertEquals(firstCompetitor.username, playerOne.username)
        assertEquals(playerOne.rating, game.playerOne.rating)
        assertEquals(playerOne.result!!.score, game.playerOne.score)
        assertEquals(secondCompetitor.id, game.playerTwo.competitorId)
        assertEquals(secondCompetitor.username, playerTwo.username)
        assertEquals(playerTwo.rating, game.playerTwo.rating)
        assertEquals(playerTwo.result!!.score, game.playerTwo.score)
    }

    @Test
    internal fun `Should throw exception when match does not exist when completing game`() {
        // given
        given(matchService.getScheduled(matchId)).willThrow(IllegalStateException::class.java)
        // then
        assertFailsWith<IllegalStateException> { service.complete(matchId, 4, 3) }
    }

    @Test
    internal fun `Should return games connection by league id`() {
        // given
        val game = Game(gameId, playerOne, playerTwo, leagueId, LocalDateTime.now())
        val side = Side(listOf(game), 1, hasPrevious = false, hasNext = false)
        given(gameRepository.findByLeagueId(leagueId, 1)).willReturn(side)
        // when
        val result: Connection<GameModel> =
            service.getConnectionForLeague(leagueId, 1, null, mock(DataFetchingEnvironment::class.java))
        // then
        assertFalse(result.pageInfo.isHasPreviousPage)
        assertFalse(result.pageInfo.isHasNextPage)
        assertEquals(1, result.edges.size)
        val edge = result.edges.first()
        assertEquals(game.id, String(Base64.getDecoder().decode(edge.cursor.value)))
        assertEquals(game.dateTime, edge.node.dateTime)
        assertEquals(game.id, edge.node.id)
        assertEquals(game.playerOne.competitorId, edge.node.playerOne.competitorId)
        assertEquals(game.playerTwo.competitorId, edge.node.playerTwo.competitorId)
    }

    @Test
    internal fun `Should return empty games connection`() {
        // given
        val side = Side(emptyList<Game>(), 0, hasPrevious = false, hasNext = false)
        given(gameRepository.findByLeagueId(leagueId, 1, "3")).willReturn(side)
        // when
        val result = service.getConnectionForLeague(leagueId, 1, "Mw==", mock(DataFetchingEnvironment::class.java))
        // then
        assertTrue(result.edges.isEmpty())
        assertFalse(result.pageInfo.isHasPreviousPage)
        assertFalse(result.pageInfo.isHasNextPage)
        println(result)
    }

    @Test
    internal fun `Should throw IllegalStateException when game id is null for connection`() {
        // given
        val firstCompetitor = Competitor(leagueId, "comp-1", "Batman")
        val secondCompetitor = Competitor(leagueId, "comp-2", "Superman")
        val game = GameFactory.completed(firstCompetitor, 2, secondCompetitor, 3, leagueId)
        val side = Side(listOf(game), 1, hasPrevious = false, hasNext = false)
        given(gameRepository.findByLeagueId(leagueId, 1)).willReturn(side)
        // when
        val exception = assertThrows<IllegalStateException> {
            service.getConnectionForLeague(leagueId, 1, null, mock(DataFetchingEnvironment::class.java))
        }
        // then
        assertEquals("Value to encode is null", exception.message)
    }
}