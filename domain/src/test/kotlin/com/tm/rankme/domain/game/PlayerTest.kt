package com.tm.rankme.domain.game

import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import org.junit.jupiter.api.Test

internal class PlayerTest {
    private val competitorId = "comp-1"
    private val username = "Optimus Prime"
    private val deviation = 248
    private val rating = 1764

    @Test
    internal fun `Should create player without result`() {
        // when
        val player = Player(competitorId, username, deviation, rating)
        // then
        assertEquals(competitorId, player.competitorId)
        assertEquals(username, player.username)
        assertEquals(deviation, player.deviation)
        assertEquals(rating, player.rating)
        assertNull(player.result)
    }

    @Test
    internal fun `Should create player with result`() {
        // given
        val score = 3
        val deviationDelta = -23
        val ratingDelta = -74
        // when
        val player = Player(competitorId, username, deviation, rating, Result(score, deviationDelta, ratingDelta))
        // then
        assertEquals(competitorId, player.competitorId)
        assertEquals(username, player.username)
        assertEquals(deviation + deviationDelta, player.deviation)
        assertEquals(rating + ratingDelta, player.rating)
        assertNotNull(player.result)
        assertEquals(score, player.result!!.score)
        assertEquals(ratingDelta, player.result!!.ratingDelta)
    }

    @Test
    internal fun `Should add result`() {
        // given
        val oldDeviation = 250
        val newDeviation = 236
        val oldRating = 1345
        val newRating = 1367
        val score = 3
        val player = Player(competitorId, username, deviation, rating)
        // when
        player.addResult(oldDeviation, newDeviation, oldRating, newRating, score)
        // then
        assertEquals(oldDeviation - 14, player.deviation)
        assertEquals(newDeviation, player.deviation)
        assertEquals(-14, player.result!!.deviationDelta)
        assertEquals(oldRating + 22, player.rating)
        assertEquals(newRating, player.rating)
        assertEquals(score, player.result!!.score)
        assertEquals(22, player.result!!.ratingDelta)
    }
}