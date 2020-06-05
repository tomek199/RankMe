package com.tm.rankme.application.league

import com.tm.rankme.application.common.Mapper
import com.tm.rankme.application.competitor.CompetitorMapper
import com.tm.rankme.application.competitor.CompetitorModel
import com.tm.rankme.application.game.GameMapper
import com.tm.rankme.application.game.GameModel
import com.tm.rankme.domain.Side
import com.tm.rankme.domain.competitor.Competitor
import com.tm.rankme.domain.competitor.CompetitorRepository
import com.tm.rankme.domain.competitor.Statistics
import com.tm.rankme.domain.game.Game
import com.tm.rankme.domain.game.GameRepository
import com.tm.rankme.domain.game.Player
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertFalse

@ExtendWith(MockitoExtension::class)
internal class LeagueResolverTest {
    private val competitorRepository: CompetitorRepository = Mockito.mock(CompetitorRepository::class.java)
    private val gameRepository: GameRepository = Mockito.mock(GameRepository::class.java)
    private val competitorMapper: Mapper<Competitor, CompetitorModel> = CompetitorMapper()
    private val gameMapper: Mapper<Game, GameModel> = GameMapper()
    private val resolver: LeagueResolver = LeagueResolver(
        competitorRepository, gameRepository, competitorMapper, gameMapper
    )
    private val league = LeagueModel("league-1", "Star Wars", LeagueSettingsModel(true, 3))
    private val competitor1 = Competitor(league.id, "comp-1", "Optimus Prime", Statistics())
    private val competitor2 = Competitor(league.id, "comp-2", "Megatron", Statistics())

    @Test
    internal fun `Should return competitors list by league id`() {
        // given
        given(competitorRepository.findByLeagueId(league.id)).willReturn(listOf(competitor1, competitor2))
        // when
        val competitors = resolver.competitors(league)
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
        given(competitorRepository.findByLeagueId(league.id)).willReturn(emptyList())
        // when
        val competitors = resolver.competitors(league)
        // then
        assertEquals(0, competitors.size)
    }

    @Test
    internal fun `Should return pageable games by league id`() {
        // given
        val game = Game(
            "game-1", Player(competitor1.id!!, competitor1.username, 243, 1435),
            Player(competitor2.id!!, competitor2.username, 243, 1435), league.id, LocalDateTime.now()
        )
        val side = Side(listOf(game), 1, hasPrevious = false, hasNext = false)
        given(gameRepository.findByLeagueId(league.id, 1)).willReturn(side)
        // when
        val result = resolver.games(league, 1, null)
        // then
        assertEquals(1, result.totalCount)
        assertFalse(result.pageInfo.hasPreviousPage)
        assertFalse(result.pageInfo.hasNextPage)
        assertEquals(1, result.edges.size)
        val edge = result.edges.first()
        assertEquals(game.id, edge.cursor)
        assertEquals(game.dateTime, edge.node.dateTime)
        assertEquals(game.id, edge.node.id)
        assertEquals(game.playerOne.competitorId, edge.node.playerOne.competitorId)
        assertEquals(game.playerTwo.competitorId, edge.node.playerTwo.competitorId)
    }
}

