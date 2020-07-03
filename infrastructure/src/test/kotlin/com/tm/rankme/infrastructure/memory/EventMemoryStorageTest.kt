package com.tm.rankme.infrastructure.memory

import com.tm.rankme.domain.event.Event
import com.tm.rankme.domain.event.EventRepository
import com.tm.rankme.domain.event.Player
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class EventMemoryStorageTest {
    private val leagueId = "league-1"
    private val player1 = Player("comp-1", "Batman", 234, 2386)
    private val player2 = Player("comp-2", "Superman", 285, 1859)

    private val repository: EventRepository = EventMemoryStorage()

    @BeforeEach
    internal fun setUp() {
        repository.save(Event(leagueId, player1, player2, LocalDateTime.now()))
        repository.save(Event(leagueId, player1, player2, LocalDateTime.now()))
    }

    @Test
    internal fun `Should save new event`() {
        // given
        val event = Event(leagueId, player1, player2, LocalDateTime.now())
        // when
        val result = repository.save(event)
        // then
        assertEquals("3", result.id)
    }

    @Test
    internal fun `Should return event by id`() {
        // given
        val event = Event(leagueId, player1, player2, LocalDateTime.now())
        val eventToFind = repository.save(event)
        // when
        val result = repository.findById("3")
        // then
        assertEquals(eventToFind.id, result?.id)
    }

    @Test
    internal fun `Should return null when event not found`() {
        // when
        val result = repository.findById("10")
        // then
        assertNull(result)
    }

    @Test
    internal fun `Should delete event from list`() {
        // given
        val event = Event(leagueId, player1, player2, LocalDateTime.now())
        val eventToDelete = repository.save(event)
        // when
        eventToDelete.id?.let { repository.delete(it) }
        // then
        assertNull(eventToDelete.id?.let { repository.findById(it) })
    }

    @Test
    internal fun `Should return events list by league id`() {
        // given
        repository.save(Event("league-2", player1, player2, LocalDateTime.now()))
        // when
        val result = repository.findByLeagueId(leagueId)
        // then
        assertEquals(2, result.size)
    }
}