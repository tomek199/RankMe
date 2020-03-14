package com.tm.rankme.infrastructure.memory

import com.tm.rankme.domain.league.League
import com.tm.rankme.domain.league.LeagueRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class LeagueMemoryStorageTest {
    private val leagueName = "Star Wars"
    private val repository: LeagueRepository = LeagueMemoryStorage()

    @BeforeEach
    internal fun setUp() {
        repository.save(League("Avengers"))
        repository.save(League("Transformers"))
    }

    @Test
    internal fun `Should save new league`() {
        // given
        val league = League(leagueName)
        // when
        val result = repository.save(league)
        // then
        assertEquals(league.name, result.name)
        assertEquals("3", result.id)
    }

    @Test
    internal fun `Should save existing league`() {
        // given
        val leagueToUpdate = repository.findById("1")
        // when
        leagueToUpdate!!.name = leagueName
        repository.save(leagueToUpdate)
        // then
        val league = repository.findById("1")
        assertEquals(leagueName, league!!.name)
    }

    @Test
    internal fun `Should return league by id`() {
        // given
        val leagueToFind = repository.save(League(leagueName))
        // when
        val result = repository.findById("3")
        // then
        assertEquals(leagueToFind.id, result?.id)
        assertEquals(leagueToFind.name, result?.name)
    }

    @Test
    internal fun `Should return null when league not found`() {
        // when
        val result = repository.findById("10")
        // then
        assertNull(result)
    }

    @Test
    internal fun `Should delete league from list`() {
        // given
        val leagueToDelete = repository.save(League(leagueName))
        // when
        leagueToDelete.id?.let { repository.delete(it) }
        // then
        assertNull(leagueToDelete.id?.let { repository.findById(it) })
    }
}