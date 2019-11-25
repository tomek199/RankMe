package com.tm.rankme.domain.rating

import com.tm.rankme.domain.player.LeagueStats
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class GlickoTest {
    @Test
    internal fun `should return correct result for new players when draw`() {
        // given
        val playerOneStats = LeagueStats("league-111")
        val playerTwoStats = LeagueStats("league-111")
        // when
        val glicko = Glicko(playerOneStats, playerTwoStats, Pair(2, 2))
        // then
        assertEquals(1500, glicko.playerOneRating())
        assertEquals(1500, glicko.playerTwoRating())
        assertEquals(290, glicko.playerOneDeviation())
        assertEquals(290, glicko.playerTwoDeviation())
    }
}