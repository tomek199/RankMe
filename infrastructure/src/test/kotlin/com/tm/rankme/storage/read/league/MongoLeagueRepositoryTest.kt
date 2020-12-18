package com.tm.rankme.storage.read.league

import com.tm.rankme.model.league.LeagueRepository
import io.mockk.every
import io.mockk.mockk
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.data.repository.findByIdOrNull

internal class MongoLeagueRepositoryTest {
    private val leagueAccessor: MongoLeagueAccessor = mockk()
    private val repository: LeagueRepository = MongoLeagueRepository(leagueAccessor)

    @Test
    internal fun `Should return league`() {
        // given
        val leagueEntity = LeagueEntity(UUID.randomUUID(), "Star Wars", false, 3)
        every { leagueAccessor.findByIdOrNull(leagueEntity.id) } returns leagueEntity
        // when
        val league = repository.byId(leagueEntity.id)
        // then
        assertTrue(league != null)
        assertEquals(leagueEntity.id, league.id)
        assertEquals(leagueEntity.name, league.name)
        assertEquals(leagueEntity.allowDraws, league.allowDraws)
        assertEquals(leagueEntity.maxScore, league.maxScore)
    }

    @Test
    internal fun `Should return null when league does not exist`() {
        // given
        val id = UUID.randomUUID()
        every { leagueAccessor.findByIdOrNull(id) } returns null
        // when
        val league = repository.byId(id)
        // then
        assertNull(league)
    }
}