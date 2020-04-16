package com.tm.rankme.application.league

import com.tm.rankme.application.common.Mapper
import com.tm.rankme.application.competitor.CompetitorMapper
import com.tm.rankme.application.competitor.CompetitorModel
import com.tm.rankme.domain.competitor.Competitor
import com.tm.rankme.domain.competitor.CompetitorRepository
import com.tm.rankme.domain.competitor.Statistics
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
internal class LeagueResolverTest {
    private val competitorRepository: CompetitorRepository = Mockito.mock(CompetitorRepository::class.java)
    private val competitorMapper: Mapper<Competitor, CompetitorModel> = CompetitorMapper()
    private val resolver: LeagueResolver = LeagueResolver(competitorRepository, competitorMapper)
    private val league = LeagueModel("league-1", "Star Wars", LeagueSettingsModel(true, 3))

    @Test
    internal fun `Should return competitors list by league id`() {
        // given
        val competitor1 = Competitor(league.id, "comp-1", "Optimus Prime", Statistics())
        val competitor2 = Competitor(league.id, "comp-2", "Megatron", Statistics())
        given(competitorRepository.findByLeagueId(league.id)).willReturn(listOf(competitor1, competitor2))
        // when
        val competitors = resolver.competitors(league)
        // then
        Assertions.assertEquals(2, competitors.size)
        Assertions.assertEquals(competitor1.id, competitors[0].id)
        Assertions.assertEquals(competitor1.leagueId, competitors[0].leagueId)
        Assertions.assertEquals(competitor1.username, competitors[0].username)
        Assertions.assertEquals(competitor2.id, competitors[1].id)
        Assertions.assertEquals(competitor2.leagueId, competitors[1].leagueId)
        Assertions.assertEquals(competitor2.username, competitors[1].username)
    }

    @Test
    internal fun `Should return empty list`() {
        // given
        given(competitorRepository.findByLeagueId(league.id)).willReturn(emptyList())
        // when
        val competitors = resolver.competitors(league)
        // then
        assertEquals(0, competitors.size)
    }
}