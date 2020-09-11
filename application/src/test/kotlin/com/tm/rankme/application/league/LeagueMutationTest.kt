package com.tm.rankme.application.league

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull

internal class LeagueMutationTest {
    private val leagueService: LeagueService = Mockito.mock(LeagueService::class.java)
    private val mutation = LeagueMutation(leagueService)
    private val leagueName = "Star Wars"

    @BeforeEach
    internal fun setUp() {
        given(leagueService.create(leagueName))
            .willReturn(LeagueModel("league-1", leagueName, LeagueSettingsModel(false, 2)))
    }

    @Test
    internal fun `Should add league with default params`() {
        // given
        val input = AddLeagueInput(leagueName)
        // when
        val league = mutation.addLeague(input)
        // then
        assertNotNull(league.id)
        assertEquals(input.name, league.name)
        assertFalse(league.settings.allowDraws)
        assertEquals(2, league.settings.maxScore)
    }
}