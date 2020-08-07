package com.tm.rankme.application.league

import com.tm.rankme.application.common.Mapper
import com.tm.rankme.domain.league.League
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class LeagueQueryTest {
    private val leagueService: LeagueService = Mockito.mock(LeagueService::class.java)
    private val mapper: Mapper<League, LeagueModel> = LeagueMapper()
    private val query = LeagueQuery(leagueService, mapper)

    @Test
    internal fun `Should return league by id`() {
        // given
        val leagueId = "league-1"
        val leagueName = "Star Wars"
        given(leagueService.get(leagueId)).willReturn(League(leagueId, leagueName))
        // when
        val league = query.league(leagueId)
        // then
        assertEquals(leagueId, league!!.id)
        assertEquals(leagueName, league.name)
        assertEquals(false, league.settings.allowDraws)
        assertEquals(2, league.settings.maxScore)
    }

    @Test
    internal fun `Should throw IllegalStateException when league is not found`() {
        // given
        val leagueId = "league-1"
        given(leagueService.get(leagueId)).willThrow(IllegalStateException::class.java)
        // then
        assertFailsWith<IllegalStateException> { query.league(leagueId) }
    }
}