package com.tm.rankme.infrastructure.memory

import com.tm.rankme.domain.competitor.Competitor
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class CompetitorMemoryStorageTest {
    private val repository = CompetitorMemoryStorage()

    @BeforeEach
    fun setUp() {
        repository.save(Competitor("l-111", "Spiderman"))
        repository.save(Competitor("l-222", "Superman"))
    }

    @Test
    internal fun `should save new competitor`() {
        // given
        val competitor = Competitor("l-111", "Batman")
        // when
        val result = repository.save(competitor)
        // then
        assertEquals(result.username, competitor.username)
        assertEquals("3", result.id)
        assertEquals(3, repository.findAll().size)
    }

    @Test
    internal fun `should save existing competitor`() {
        // given
        val competitorToUpdate = repository.findById("1")
        val newUsername = "Batman"
        // when
        competitorToUpdate!!.username = newUsername
        repository.save(competitorToUpdate)
        // then
        val competitor = repository.findById("1")
        assertEquals(newUsername, competitor!!.username)
        assertEquals(2, repository.findAll().size)
    }

    @Test
    internal fun `should return competitors list`() {
        // when
        val result = repository.findAll()
        // then
        assertEquals(2, result.size)
    }

    @Test
    internal fun `should return competitor by id`() {
        // given
        val competitorToFind = repository.save(Competitor("l-111", "Batman"))
        // when
        val result = repository.findById("3")
        // then
        assertEquals(competitorToFind.id, result?.id)
        assertEquals(competitorToFind.username, result?.username)
    }

    @Test
    internal fun `should return null when competitor not found`() {
        // when
        val result = repository.findById("10")
        // then
        assertNull(result)
    }

    @Test
    internal fun `should delete competitor from list`() {
        // given
        val competitorToDelete = repository.save(Competitor("l-111", "Batman"))
        // when
        competitorToDelete.id?.let { repository.delete(it) }
        // then
        assertEquals(2, repository.findAll().size)
        assertNull(competitorToDelete.id?.let { repository.findById(it) })
    }
}