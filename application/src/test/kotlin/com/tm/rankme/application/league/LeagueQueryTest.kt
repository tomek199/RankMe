package com.tm.rankme.application.league

import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class LeagueQueryTest {
    private val leagueService: LeagueService = Mockito.mock(LeagueService::class.java)
    private val query = LeagueQuery(leagueService)

    @Test
    internal fun `Should return league by id`() {
        // given
        val leagueId = "league-1"
        val leagueName = "Star Wars"
        given(leagueService.get(leagueId))
            .willReturn(LeagueModel(leagueId, leagueName, LeagueSettingsModel(false, 2)))
        // when
        val league: LeagueModel? = query.league(leagueId)
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