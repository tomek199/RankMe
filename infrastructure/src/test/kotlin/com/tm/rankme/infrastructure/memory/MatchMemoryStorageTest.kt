package com.tm.rankme.infrastructure.memory

import com.tm.rankme.domain.match.Match
import com.tm.rankme.domain.match.MatchRepository
import com.tm.rankme.domain.match.Member
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class MatchMemoryStorageTest {
    private val leagueId = "league-1"
    private val member1 = Member("comp-1", "Batman", 234, 2386)
    private val member2 = Member("comp-2", "Superman", 285, 1859)

    private val repository: MatchRepository = MatchMemoryStorage()

    @BeforeEach
    internal fun setUp() {
        repository.save(Match(leagueId, member1, member2, LocalDateTime.now()))
        repository.save(Match(leagueId, member1, member2, LocalDateTime.now()))
    }

    @Test
    internal fun `Should save new match`() {
        // given
        val match = Match(leagueId, member1, member2, LocalDateTime.now())
        // when
        val result = repository.save(match)
        // then
        assertEquals("3", result.id)
    }

    @Test
    internal fun `Should save existing match`() {
        // given
        val match = repository.findById("1")
        // when
        val result = repository.save(match!!)
        // then
        assertEquals("1", result.id)
    }

    @Test
    internal fun `Should return match by id`() {
        // given
        val match = Match(leagueId, member1, member2, LocalDateTime.now())
        val matchToFind = repository.save(match)
        // when
        val result = repository.findById("3")
        // then
        assertEquals(matchToFind.id, result?.id)
    }

    @Test
    internal fun `Should return null when match not found`() {
        // when
        val result = repository.findById("10")
        // then
        assertNull(result)
    }

    @Test
    internal fun `Should delete match from list`() {
        // given
        val match = Match(leagueId, member1, member2, LocalDateTime.now())
        val matchToDelete = repository.save(match)
        // when
        matchToDelete.id?.let { repository.delete(it) }
        // then
        assertNull(matchToDelete.id?.let { repository.findById(it) })
    }

    @Test
    internal fun `Should return matches list by league id`() {
        // given
        repository.save(Match("league-2", member1, member2, LocalDateTime.now()))
        // when
        val result = repository.findByLeagueId(leagueId)
        // then
        assertEquals(2, result.size)
    }
}