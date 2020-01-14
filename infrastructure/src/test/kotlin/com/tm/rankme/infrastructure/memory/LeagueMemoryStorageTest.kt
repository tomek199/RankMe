package com.tm.rankme.infrastructure.memory

import com.tm.rankme.domain.league.League
import com.tm.rankme.domain.league.LeagueRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class LeagueMemoryStorageTest {
    private val repository: LeagueRepository = LeagueMemoryStorage()

    @BeforeEach
    internal fun setUp() {
        repository.save(League("Avengers"))
        repository.save(League("Transformers"))
    }

    @Test
    internal fun `should save league`() {
        // given
        val league = League("Star Wars")
        // when
        val result = repository.save(league)
        // then
        assertEquals(league.name, result.name)
        assertEquals("3", result.id)
        assertEquals(3, repository.findAll().size)
    }

    @Test
    internal fun `should return leagues list`() {
        // when
        val result = repository.findAll()
        // then
        assertEquals(2, result.size)
    }

    @Test
    internal fun `should return league by id`() {
        // given
        val leagueToFind = repository.save(League("Star Wars"))
        // when
        val result = repository.findById("3")
        // then
        assertEquals(leagueToFind.id, result?.id)
        assertEquals(leagueToFind.name, result?.name)
    }

    @Test
    internal fun `should return null when league not found`() {
        // when
        val result = repository.findById("10")
        // then
        assertNull(result)
    }

    @Test
    internal fun `should delete league from list`() {
        // given
        val leagueToDelete = repository.save(League("Star Wars"))
        // when
        leagueToDelete.id?.let { repository.delete(it) }
        // then
        assertEquals(2, repository.findAll().size)
        assertNull(leagueToDelete.id?.let { repository.findById(it) })
    }
}