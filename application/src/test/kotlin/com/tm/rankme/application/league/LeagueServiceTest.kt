package com.tm.rankme.application.league

import com.tm.rankme.application.any
import com.tm.rankme.application.common.Mapper
import com.tm.rankme.domain.league.League
import com.tm.rankme.domain.league.LeagueRepository
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import org.mockito.Mockito.only
import org.mockito.Mockito.verify
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class LeagueServiceTest {
    private val repository: LeagueRepository = Mockito.mock(LeagueRepository::class.java)
    private val mapper: Mapper<League, LeagueModel> = LeagueMapper()
    private val service: LeagueService = LeagueServiceImpl(repository, mapper)
    private val leagueId = "league-1"
    private val leagueName = "Star Wars"


    @Test
    internal fun `Should return league by id`() {
        // given
        given(repository.findById(leagueId)).willReturn(League(leagueId, leagueName))
        // when
        val league = service.get(leagueId)
        // then
        assertEquals(leagueId, league.id)
        assertEquals(leagueName, league.name)
        assertEquals(false, league.settings.allowDraws)
        assertEquals(2, league.settings.maxScore)
    }

    @Test
    internal fun `Should throw IllegalStateException when league does not exist`() {
        // given
        given(repository.findById(leagueId)).willReturn(null)
        // when
        val exception = assertFailsWith<IllegalStateException> { service.get(leagueId) }
        // then
        assertEquals("League $leagueId is not found", exception.message)
    }

    @Test
    internal fun `Should create league`() {
        // given
        val expectedLeague = League(leagueId, leagueName)
        given(repository.save(any(League::class.java))).willReturn(expectedLeague)
        // when
        val league: LeagueModel = service.create(leagueName)
        // then
        assertEquals(expectedLeague.id, league.id)
        assertEquals(expectedLeague.name, league.name)
        assertEquals(expectedLeague.settings.allowDraws, league.settings.allowDraws)
        assertEquals(expectedLeague.settings.maxScore, league.settings.maxScore)
        verify(repository, only()).save(any(League::class.java))
    }

    @Test
    internal fun `Should do nothing when league exist during check`() {
        // given
        given(repository.findById(leagueId)).willReturn(League(leagueId, leagueName))
        // when
        service.checkIfExist(leagueId)
        // then
        verify(repository, only()).findById(leagueId)
    }

    @Test
    internal fun `Should throw IllegalStateException when league does not exist during check`() {
        // given
        given(repository.findById(leagueId)).willReturn(null)
        // when
        val exception = assertFailsWith<IllegalStateException> { service.checkIfExist(leagueId) }
        // then
        verify(repository, only()).findById(leagueId)
        assertEquals("League $leagueId is not found", exception.message)
    }
}
