package com.tm.rankme.infrastructure.league

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.capture
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.tm.rankme.domain.base.Event
import com.tm.rankme.domain.league.League
import com.tm.rankme.domain.league.LeagueCreated
import com.tm.rankme.domain.league.LeagueRenamed
import com.tm.rankme.domain.league.LeagueSettingsChanged
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test

internal class EventSourceLeagueRepositoryTest {
    private val eventStorage: LeagueEventStorage = mock()
    private val repository = EventSourceLeagueRepository(eventStorage)

    @Test
    internal fun `Should get league by id`() {
        // given
        val aggregateId = UUID.randomUUID()
        val created = LeagueCreated("Star Wars", aggregateId = aggregateId)
        val renamed = LeagueRenamed(aggregateId, 1, "Transformers")
        val settingsChanged = LeagueSettingsChanged(aggregateId, 2, true, 7)
        given(eventStorage.events(aggregateId.toString())).willReturn(listOf(created, renamed, settingsChanged))
        // when
        val league = repository.byId(aggregateId)
        // then
        assertEquals(aggregateId, league.id)
        assertEquals(2, league.version)
        assertTrue(league.pendingEvents.isEmpty())
        assertEquals(renamed.name, league.name)
        assertEquals(settingsChanged.allowDraws, league.settings.allowDraws)
        assertEquals(settingsChanged.maxScore, league.settings.maxScore)
    }

    @Test
    internal fun `Should store league`() {
        // given
        val league = League.create("Star Wars")
        league.settings(true, 7)
        league.rename("Transformers")
        // when
        repository.store(league)
        // then
        verify(eventStorage, times(3)).save(any())
    }
}