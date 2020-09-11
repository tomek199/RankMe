package com.tm.rankme.application.league

import com.tm.rankme.application.common.Mapper
import com.tm.rankme.application.competitor.CompetitorMapper
import com.tm.rankme.application.competitor.CompetitorModel
import com.tm.rankme.application.competitor.CompetitorService
import com.tm.rankme.application.competitor.CompetitorStatisticsModel
import com.tm.rankme.application.game.GameMapper
import com.tm.rankme.application.game.GameModel
import com.tm.rankme.application.game.GameService
import com.tm.rankme.domain.Side
import com.tm.rankme.domain.competitor.Competitor
import com.tm.rankme.domain.competitor.Statistics
import com.tm.rankme.domain.game.Game
import com.tm.rankme.domain.game.GameFactory
import com.tm.rankme.domain.game.Player
import graphql.schema.DataFetchingEnvironment
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class LeagueResolverTest {
    private val competitorService: CompetitorService = Mockito.mock(CompetitorService::class.java)
    private val gameService: GameService = Mockito.mock(GameService::class.java)
    private val gameMapper: Mapper<Game, GameModel> = GameMapper()
    private val resolver: LeagueResolver = LeagueResolver(competitorService, gameService, gameMapper)

    private val league = LeagueModel("league-1", "Star Wars", LeagueSettingsModel(true, 3))
    private val competitor1 = Competitor(league.id, "comp-1", "Optimus Prime", Statistics())
    private val competitor2 = Competitor(league.id, "comp-2", "Megatron", Statistics())

    @Test
    internal fun `Should return competitors list by league id`() {
        // given
        val statisticsModel1 = CompetitorStatisticsModel(250, 1500, 0, 0, 0, LocalDate.now())
        val competitorModel1 = CompetitorModel( "comp-1", "Optimus Prime", statisticsModel1)
        val statisticsModel2 = CompetitorStatisticsModel(250, 1500, 0, 0, 0, LocalDate.now())
        val competitorModel2 = CompetitorModel("comp-2", "Megatron", statisticsModel2)
        given(competitorService.getListForLeague(league.id)).willReturn(listOf(competitorModel1, competitorModel2))
        // when
        val competitors: List<CompetitorModel> = resolver.competitors(league)
        // then
        assertEquals(2, competitors.size)
        assertEquals(competitor1.id, competitors[0].id)
        assertEquals(competitor1.username, competitors[0].username)
        assertEquals(competitor2.id, competitors[1].id)
        assertEquals(competitor2.username, competitors[1].username)
    }

    @Test
    internal fun `Should return empty competitors list`() {
        // given
        given(competitorService.getListForLeague(league.id)).willReturn(emptyList())
        // when
        val competitors: List<CompetitorModel> = resolver.competitors(league)
        // then
        assertEquals(0, competitors.size)
    }

    @Test
    internal fun `Should return games connection by league id with one result`() {
        // given
        val game = Game(
            "game-1", Player(competitor1.id!!, competitor1.username, 243, 1435, 4, 75),
            Player(competitor2.id!!, competitor2.username, 243, 1435, 3, -75), league.id, LocalDateTime.now()
        )
        val side = Side(listOf(game), 1, hasPrevious = false, hasNext = false)
        given(gameService.getSideForLeague(league.id, 1)).willReturn(side)
        // when
        val result = resolver.games(league, 1, null, Mockito.mock(DataFetchingEnvironment::class.java))
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
        given(gameService.getSideForLeague(league.id, 1, "3")).willReturn(side)
        // when
        val result = resolver.games(league, 1, "Mw==", Mockito.mock(DataFetchingEnvironment::class.java))
        // then
        assertTrue(result.edges.isEmpty())
        assertFalse(result.pageInfo.isHasPreviousPage)
        assertFalse(result.pageInfo.isHasNextPage)
        println(result)
    }

    @Test
    internal fun `Should throw IllegalStateException when game id is null`() {
        // given
        val game = GameFactory.create(competitor1, 2, competitor2, 3, league.id)
        val side = Side(listOf(game), 1, hasPrevious = false, hasNext = false)
        given(gameService.getSideForLeague(league.id, 1)).willReturn(side)
        // when
        val exception = assertThrows<IllegalStateException> {
            resolver.games(league, 1, null, Mockito.mock(DataFetchingEnvironment::class.java))
        }
        // then
        assertEquals("Value to encode is null", exception.message)
    }
}
