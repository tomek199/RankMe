package com.tm.rankme.application.match

import com.tm.rankme.application.any
import com.tm.rankme.application.common.Mapper
import com.tm.rankme.application.competitor.CompetitorService
import com.tm.rankme.domain.competitor.Competitor
import com.tm.rankme.domain.match.Match
import com.tm.rankme.domain.match.MatchRepository
import com.tm.rankme.domain.match.Member
import com.tm.rankme.domain.match.Status
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.times
import org.mockito.Mockito.mock
import org.mockito.Mockito.only
import org.mockito.Mockito.verify
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

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
    internal fun `Should get scheduled match`() {
        // given
        val expectedMatch = Match(matchId, leagueId, memberOne, memberTwo, LocalDateTime.now())
        given(repository.findById(matchId)).willReturn(expectedMatch)
        // when
        val match = service.getScheduled(matchId)
        // then
        assertEquals(expectedMatch.id, match.id)
        assertEquals(expectedMatch.leagueId, match.leagueId)
        assertEquals(expectedMatch.memberOne, match.memberOne)
        assertEquals(expectedMatch.memberTwo, match.memberTwo)
        assertEquals(expectedMatch.dateTime, match.dateTime)
        assertEquals(Status.SCHEDULED, match.status)
        assertNull(match.gameId)
        verify(repository, only()).findById(matchId)
    }

    @Test
    internal fun `Should throw IllegalStateException when get scheduled match`() {
        // given
        val completedMatch = Match(matchId, leagueId, memberOne, memberTwo, LocalDateTime.now())
        completedMatch.complete("game-1")
        given(repository.findById(matchId)).willReturn(completedMatch)
        // when
        val exception = assertFailsWith<IllegalStateException> { service.getScheduled(matchId) }
        // then
        assertEquals("Match $matchId is not in ${Status.SCHEDULED} state", exception.message)
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
        val firstCompetitorId = "comp-1"
        val firstCompetitor = Competitor(leagueId, firstCompetitorId, "Batman", 243, 2945)
        val secondCompetitorId = "comp-2"
        val secondCompetitor = Competitor(leagueId, secondCompetitorId, "Superman", 195, 2877)
        val matchDateTime = LocalDateTime.now()
        val expectedMatch = Match(
            matchId, leagueId,
            Member(
                firstCompetitorId, firstCompetitor.username,
                firstCompetitor.deviation, firstCompetitor.rating),
            Member(
                secondCompetitorId, secondCompetitor.username,
                secondCompetitor.deviation, secondCompetitor.rating),
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
        assertEquals(firstCompetitor.deviation, match.memberOne.deviation)
        assertEquals(firstCompetitor.rating, match.memberOne.rating)
        assertEquals(secondCompetitor.id, match.memberTwo.competitorId)
        assertEquals(secondCompetitor.username, match.memberTwo.username)
        assertEquals(secondCompetitor.deviation, match.memberTwo.deviation)
        assertEquals(secondCompetitor.rating, match.memberTwo.rating)
    }

    @Test
    internal fun `Should throw IllegalStateException when first competitor does not have id`() {
        // given
        val firstCompetitorId = "comp-1"
        val firstCompetitor = Competitor(leagueId, "Batman")
        val secondCompetitorId = "comp-2"
        val secondCompetitor = Competitor(leagueId, secondCompetitorId, "Superman")
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
        val firstCompetitor = Competitor(leagueId, firstCompetitorId, "Batman")
        val secondCompetitorId = "comp-2"
        val secondCompetitor = Competitor(leagueId, "Superman")
        given(competitorService.getForLeague(firstCompetitorId, leagueId)).willReturn(firstCompetitor)
        given(competitorService.getForLeague(secondCompetitorId, leagueId)).willReturn(secondCompetitor)
        // when
        val exception = assertFailsWith<IllegalStateException> {
            service.create(leagueId, firstCompetitorId, secondCompetitorId, LocalDateTime.now())
        }
        assertEquals("Competitor ${secondCompetitor.username} id is null", exception.message)
    }

    @Test
    internal fun `Should complete match`() {
        // given
        val dateTime = LocalDateTime.now()
        val matchToComplete = Match(matchId, leagueId, memberOne, memberTwo, dateTime)
        given(repository.findById(matchId)).willReturn(matchToComplete)
        val gameId = "game-1"
        // when
        service.complete(matchId, gameId)
        // then
        verify(repository, times(1)).findById(matchId)
        verify(repository, times(1)).save(any(Match::class.java))
    }

    @Test
    internal fun `Should throw IllegalStateException when match does not exist during completing`() {
        // given
        given(repository.findById(matchId)).willReturn(null)
        // when
        val exception = assertFailsWith<IllegalStateException> { service.complete(matchId, "game-1") }
        // then
        assertEquals("Match $matchId is not found", exception.message)
    }
}