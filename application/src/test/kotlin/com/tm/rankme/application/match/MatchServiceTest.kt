package com.tm.rankme.application.match

import com.tm.rankme.application.any
import com.tm.rankme.application.common.Mapper
import com.tm.rankme.application.competitor.CompetitorService
import com.tm.rankme.domain.competitor.Competitor
import com.tm.rankme.domain.competitor.Statistics
import com.tm.rankme.domain.match.Match
import com.tm.rankme.domain.match.MatchRepository
import com.tm.rankme.domain.match.Member
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import org.mockito.Mockito.only
import org.mockito.Mockito.verify
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class MatchServiceTest {
    private val repository: MatchRepository = mock(MatchRepository::class.java)
    private val competitorService: CompetitorService = mock(CompetitorService::class.java)
    private val mapper: Mapper<Match, MatchModel> = MatchMapper()
    private val service: MatchService = MatchServiceImpl(repository, competitorService, mapper)

    private val matchId = "match-1"
    private val leagueId = "league-1"
    private val memberOne = Member("comp-1", "Batman", 234, 2435)
    private val memberTwo = Member("comp-2", "Superman", 315, 1295)

    @Test
    internal fun `Should get match`() {
        // given
        val expectedMatch = Match(matchId, leagueId, memberOne, memberTwo, LocalDateTime.now())
        given(repository.findById(matchId)).willReturn(expectedMatch)
        // when
        val match = service.get(matchId)
        // then
        assertEquals(expectedMatch.id, match.id)
        assertEquals(expectedMatch.leagueId, match.leagueId)
        assertEquals(expectedMatch.memberOne, match.memberOne)
        assertEquals(expectedMatch.memberTwo, match.memberTwo)
        assertEquals(expectedMatch.dateTime, match.dateTime)
        verify(repository, only()).findById(matchId)
    }

    @Test
    internal fun `Should throw IllegalStateException when match does not exist`() {
        // given
        given(repository.findById(matchId)).willReturn(null)
        // when
        val exception = assertFailsWith<IllegalStateException> { service.get(matchId) }
        // then
        assertEquals("Match $matchId is not found", exception.message)
    }

    @Test
    internal fun `Should create match`() {
        // given
        val firstCompetitorStats = Statistics(243, 2945, 3, 5, 9, LocalDate.now())
        val firstCompetitorId = "comp-1"
        val firstCompetitor = Competitor(leagueId, firstCompetitorId, "Batman", firstCompetitorStats)
        val secondCompetitorStats = Statistics(195, 2877, 8, 7, 2, LocalDate.now())
        val secondCompetitorId = "comp-2"
        val secondCompetitor = Competitor(leagueId, secondCompetitorId, "Superman", secondCompetitorStats)
        val matchDateTime = LocalDateTime.now()
        val expectedMatch = Match(
            matchId, leagueId,
            Member(
                firstCompetitorId, firstCompetitor.username,
                firstCompetitor.statistics.deviation, firstCompetitor.statistics.rating),
            Member(
                secondCompetitorId, secondCompetitor.username,
                secondCompetitor.statistics.deviation, secondCompetitor.statistics.rating),
            matchDateTime
        )
        given(competitorService.getForLeague(firstCompetitorId, leagueId)).willReturn(firstCompetitor)
        given(competitorService.getForLeague(secondCompetitorId, leagueId)).willReturn(secondCompetitor)
        given(repository.save(any(Match::class.java))).willReturn(expectedMatch)
        // when
        val match: MatchModel = service.create(leagueId, firstCompetitorId, secondCompetitorId, matchDateTime)
        // then
        verify(repository, only()).save(any(Match::class.java))
        assertEquals(matchDateTime, match.dateTime)
        assertEquals(firstCompetitor.id, match.memberOne.competitorId)
        assertEquals(firstCompetitor.username, match.memberOne.username)
        assertEquals(firstCompetitor.statistics.deviation, match.memberOne.deviation)
        assertEquals(firstCompetitor.statistics.rating, match.memberOne.rating)
        assertEquals(secondCompetitor.id, match.memberTwo.competitorId)
        assertEquals(secondCompetitor.username, match.memberTwo.username)
        assertEquals(secondCompetitor.statistics.deviation, match.memberTwo.deviation)
        assertEquals(secondCompetitor.statistics.rating, match.memberTwo.rating)
    }

    @Test
    internal fun `Should throw IllegalStateException when first competitor does not have id`() {
        // given
        val firstCompetitorId = "comp-1"
        val firstCompetitor = Competitor(leagueId, "Batman", Statistics())
        val secondCompetitorId = "comp-2"
        val secondCompetitor = Competitor(leagueId, secondCompetitorId, "Superman", Statistics())
        given(competitorService.getForLeague(firstCompetitorId, leagueId)).willReturn(firstCompetitor)
        given(competitorService.getForLeague(secondCompetitorId, leagueId)).willReturn(secondCompetitor)
        // when
        val exception = assertFailsWith<IllegalStateException> {
            service.create(leagueId, firstCompetitorId, secondCompetitorId, LocalDateTime.now())
        }
        assertEquals("Competitor ${firstCompetitor.username} id is null", exception.message)
    }

    @Test
    internal fun `Should throw IllegalStateException when second competitor does not have id`() {
        // given
        val firstCompetitorId = "comp-1"
        val firstCompetitor = Competitor(leagueId, firstCompetitorId, "Batman", Statistics())
        val secondCompetitorId = "comp-2"
        val secondCompetitor = Competitor(leagueId, "Superman", Statistics())
        given(competitorService.getForLeague(firstCompetitorId, leagueId)).willReturn(firstCompetitor)
        given(competitorService.getForLeague(secondCompetitorId, leagueId)).willReturn(secondCompetitor)
        // when
        val exception = assertFailsWith<IllegalStateException> {
            service.create(leagueId, firstCompetitorId, secondCompetitorId, LocalDateTime.now())
        }
        assertEquals("Competitor ${secondCompetitor.username} id is null", exception.message)
    }

    @Test
    internal fun `Should remove match`() {
        // given
        val matchId = "match-1"
        // when
        service.remove(matchId)
        // then
        verify(repository, only()).delete(matchId)
    }
}