package com.tm.rankme.application.league

import com.tm.rankme.application.any
import com.tm.rankme.application.common.Mapper
import com.tm.rankme.domain.league.League
import com.tm.rankme.domain.league.LeagueRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull

@ExtendWith(MockitoExtension::class)
internal class LeagueMutationTest {
    private val repository: LeagueRepository = Mockito.mock(LeagueRepository::class.java)
    private val mapper: Mapper<League, LeagueModel> = LeagueMapper()
    private val mutation = LeagueMutation(repository, mapper)

    @BeforeEach
    internal fun setUp() {
        given(repository.save(any(League::class.java))).willReturn(League("league-1", "Star Wars"))
    }

    @Test
    internal fun `Should add league with default params`() {
        // given
        val name = "Star Wars"
        // when
        val league = mutation.addLeague(name)
        // then
        assertNotNull(league.id)
        assertEquals(name, league.name)
        assertFalse(league.settings.allowDraws)
        assertEquals(2, league.settings.maxScore)
    }
}