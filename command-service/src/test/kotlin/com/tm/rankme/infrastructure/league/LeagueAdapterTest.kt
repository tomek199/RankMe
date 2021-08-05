package com.tm.rankme.infrastructure.league

import com.aventrix.jnanoid.jnanoid.NanoIdUtils.randomNanoId
import com.tm.rankme.domain.league.LeagueRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class LeagueAdapterTest {
    private val repository = mockk<LeagueRepository>()
    private val adapter = LeagueAdapter(repository)

    @Test
    internal fun `Should return true when league exist`() {
        // given
        val leagueId = randomNanoId()
        every { repository.exist(leagueId) } returns true
        // then
        assertTrue(adapter.exist(leagueId))
    }

    @Test
    internal fun `Should return false when league does not exist`() {
        // given
        val leagueId = randomNanoId()
        every { repository.exist(leagueId) } returns false
        // then
        assertFalse(adapter.exist(leagueId))
    }
}