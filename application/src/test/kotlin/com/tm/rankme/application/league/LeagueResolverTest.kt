package com.tm.rankme.application.league

import com.tm.rankme.application.competitor.CompetitorModel
import com.tm.rankme.application.competitor.CompetitorService
import com.tm.rankme.application.competitor.CompetitorStatisticsModel
import com.tm.rankme.application.game.GameModel
import com.tm.rankme.application.game.GameService
import com.tm.rankme.domain.competitor.Competitor
import com.tm.rankme.domain.competitor.Statistics
import graphql.relay.Connection
import graphql.schema.DataFetchingEnvironment
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.only
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

internal class LeagueResolverTest {
    private val competitorService: CompetitorService = mock(CompetitorService::class.java)
    private val gameService: GameService = mock(GameService::class.java)
    private val resolver: LeagueResolver = LeagueResolver(competitorService, gameService)

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
    internal fun `Should return games connection by league id`() {
        // given
        val envMock = mock(DataFetchingEnvironment::class.java)
        @Suppress("UNCHECKED_CAST")
        given(gameService.getConnectionForLeague(league.id, 1, null, envMock))
            .willReturn(mock(Connection::class.java) as Connection<GameModel>?)
        // when
        val result = resolver.games(league, 1, null, envMock)
        // then
        assertNotNull(result)
        verify(gameService, only()).getConnectionForLeague(league.id, 1, null, envMock)
    }
}
