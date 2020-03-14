package com.tm.rankme.infrastructure.memory

import com.tm.rankme.domain.competitor.Competitor
import com.tm.rankme.domain.competitor.Statistics
import com.tm.rankme.domain.game.GameFactory
import com.tm.rankme.domain.game.GameRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class GameMemoryStorageTest {
    private val leagueId = "league-1"
    private val competitor1 = Competitor(leagueId, "comp-1", "Spiderman", Statistics())
    private val competitor2 = Competitor(leagueId, "comp-2", "Superman", Statistics())

    private val repository: GameRepository = GameMemoryStorage()

    @BeforeEach
    internal fun setUp() {
        repository.save(GameFactory.completedMatch(Pair(competitor1, 2), Pair(competitor2, 1), leagueId))
        repository.save(GameFactory.completedMatch(Pair(competitor1, 1), Pair(competitor2, 2), leagueId))
    }

    @Test
    internal fun `Should save new game`() {
        // given
        val game = GameFactory.completedMatch(Pair(competitor1, 3), Pair(competitor2, 3), leagueId)
        // when
        val result = repository.save(game)
        // then
        assertEquals("3", result.id)
    }

    @Test
    internal fun `Should save existing game`() {
        // given
        val gameToUpdate = repository.findById("1")
        val newDateTime = LocalDateTime.now().minusMinutes(10)
        gameToUpdate!!.dateTime = newDateTime
        // when
        repository.save(gameToUpdate)
        // then
        val game = repository.findById("1")
        assertEquals(newDateTime, game!!.dateTime)
    }

    @Test
    internal fun `Should return game by id`() {
        // given
        val game = GameFactory.completedMatch(Pair(competitor1, 3), Pair(competitor2, 3), leagueId)
        val gameToFind = repository.save(game)
        // when
        val result = repository.findById("3")
        // then
        assertEquals(gameToFind.id, result?.id)
    }

    @Test
    internal fun `Should return null when game not found`() {
        // when
        val result = repository.findById("10")
        // then
        assertNull(result)
    }

    @Test
    internal fun `Should delete game from list`() {
        // given
        val game = GameFactory.completedMatch(Pair(competitor1, 3), Pair(competitor2, 3), leagueId)
        val gameToDelete = repository.save(game)
        // when
        gameToDelete.id?.let { repository.delete(it) }
        // then
        assertNull(gameToDelete.id?.let { repository.findById(it) })
    }
}