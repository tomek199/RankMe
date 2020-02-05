package com.tm.rankme.application.league

import com.tm.rankme.application.Mapper
import com.tm.rankme.domain.league.League
import com.tm.rankme.domain.league.LeagueRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import kotlin.test.assertEquals
import kotlin.test.assertNull

@ExtendWith(MockitoExtension::class)
internal class LeagueQueryTest {
    private val repository: LeagueRepository = Mockito.mock(LeagueRepository::class.java)
    private val mapper: Mapper<League, LeagueModel> = LeagueMapper()
    private val query = LeagueQuery(repository, mapper)

    @Test
    internal fun `should return league by id`() {
        // given
        val leagueId = "l-111"
        val leagueName = "Star Wars"
        given(repository.findById(leagueId)).willReturn(League(leagueId, leagueName))
        // when
        val league = query.league(leagueId)
        // then
        assertEquals(leagueId, league!!.id)
        assertEquals(leagueName, league.name)
        assertEquals(false, league.settings.allowDraws)
        assertEquals(2, league.settings.maxScore)
    }

    @Test
    internal fun `should return null when league not found`() {
        // given
        val leagueId = "l-111"
        given(repository.findById(leagueId)).willReturn(null)
        // when
        val league = query.league(leagueId)
        // then
        assertNull(league)
    }
}