package com.tm.rankme.domain.league

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue


internal class LeagueTest {
    private val leagueCreated = LeagueCreated("Star Wars")

    @Test
    internal fun `Should create league`() {
        // given
        val name = "Star Wars"
        // when
        val league = League.create(name)
        // then
        assertNotNull(league.id)
        assertEquals(0, league.version)
        assertEquals(name, league.name)
        assertEquals(false, league.settings.allowDraws)
        assertEquals(2, league.settings.maxScore)
        assertEquals(1, league.pendingEvents.size)
        assertTrue(league.pendingEvents[0] is LeagueCreated)
        assertEquals(0, league.pendingEvents[0].version)
    }

    @Test
    internal fun `Should change league name`() {
        // given
        val league = League.from(listOf(leagueCreated))
        val newLeagueName = "Transformers"
        // when
        league.rename(newLeagueName)
        // then
        assertEquals(1, league.version)
        assertEquals(newLeagueName, league.name)
        assertEquals(1, league.pendingEvents.size)
        assertTrue(league.pendingEvents[0] is LeagueRenamed)
        assertEquals(league.id, league.pendingEvents[0].aggregateId)
        assertEquals(1, league.pendingEvents[0].version)
    }

    @Test
    internal fun `Should change league settings`() {
        // given
        val league = League.from(listOf(leagueCreated))
        val newAllowDraws = true
        val newMaxScore = 5
        // when
        league.settings(newAllowDraws, newMaxScore)
        // then
        assertEquals(1, league.version)
        assertEquals(leagueCreated.name, league.name)
        assertEquals(newAllowDraws, league.settings.allowDraws)
        assertEquals(newMaxScore, league.settings.maxScore)
        assertEquals(1, league.pendingEvents.size)
        assertTrue(league.pendingEvents[0] is LeagueSettingsChanged)
        assertEquals(league.id, league.pendingEvents[0].aggregateId)
        assertEquals(1, league.pendingEvents[0].version)
    }

    @Test
    internal fun `Should init league aggregate from 'league-created' event`() {
        // given
        val event = LeagueCreated("Star Wars")
        // when
        val league = League.from(listOf(event))
        // then
        assertEquals(event.aggregateId, league.id)
        assertEquals(event.version, league.version)
        assertEquals(event.name, league.name)
        assertEquals(event.allowDraws, league.settings.allowDraws)
        assertEquals(event.maxScore, league.settings.maxScore)
        assertTrue(league.pendingEvents.isEmpty())
    }

    @Test
    internal fun `Should init league aggregate from all events`() {
        // given
        val nameChanged = LeagueRenamed(leagueCreated.aggregateId, 1, "Transformers")
        val settingsChanged = LeagueSettingsChanged(leagueCreated.aggregateId, 2, true, 4)
        // when
        val league = League.from(listOf(leagueCreated, nameChanged, settingsChanged))
        // then
        assertEquals(leagueCreated.aggregateId, league.id)
        assertEquals(settingsChanged.version, league.version)
        assertEquals(nameChanged.name, league.name)
        assertEquals(settingsChanged.allowDraws, league.settings.allowDraws)
        assertEquals(settingsChanged.maxScore, league.settings.maxScore)
        assertTrue(league.pendingEvents.isEmpty())
    }
}