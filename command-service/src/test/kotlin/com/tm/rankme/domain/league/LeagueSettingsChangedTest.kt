package com.tm.rankme.domain.league

import com.aventrix.jnanoid.jnanoid.NanoIdUtils.randomNanoId
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

internal class LeagueSettingsChangedTest {
    @Test
    internal fun `Should create event`() {
        // given
        val aggregateId = randomNanoId()
        val version = 2L
        val allowDraws = false
        val maxScore = 5
        // when
        val event = LeagueSettingsChanged(aggregateId, version, allowDraws, maxScore)
        // then
        assertEquals("league-settings-changed", event.type)
        assertEquals(aggregateId, event.aggregateId)
        assertEquals(version, event.version)
        assertNotNull(event.timestamp)
        assertEquals(allowDraws, event.allowDraws)
        assertEquals(maxScore, event.maxScore)
    }
}