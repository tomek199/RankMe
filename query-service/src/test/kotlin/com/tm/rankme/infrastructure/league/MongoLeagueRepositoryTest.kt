package com.tm.rankme.infrastructure.league

import com.aventrix.jnanoid.jnanoid.NanoIdUtils.randomNanoId
import com.tm.rankme.model.league.League
import com.tm.rankme.model.league.LeagueRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.data.repository.findByIdOrNull
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal class MongoLeagueRepositoryTest {
    private val accessor: MongoLeagueAccessor = mockk()
    private val repository: LeagueRepository = MongoLeagueRepository(accessor)

    @Test
    internal fun `Should return league`() {
        // given
        val leagueEntity = LeagueEntity(randomNanoId(), "Star Wars", false, 3)
        every { accessor.findByIdOrNull(leagueEntity.id) } returns leagueEntity
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
        val id = randomNanoId()
        every { accessor.findByIdOrNull(id) } returns null
        // when
        val league = repository.byId(id)
        // then
        assertNull(league)
    }

    @Test
    internal fun `Should store league`() {
        // given
        every { accessor.save(ofType(LeagueEntity::class)) } returns mockk()
        val league = League(randomNanoId(), "Star Wars", true, 3)
        // when
        repository.store(league)
        // then
        val entitySlot = slot<LeagueEntity>()
        verify(exactly = 1) { accessor.save(capture(entitySlot)) }
        entitySlot.captured.let {
            assertEquals(league.id, it.id)
            assertEquals(league.name, it.name)
            assertEquals(league.allowDraws, it.allowDraws)
            assertEquals(league.maxScore, it.maxScore)
        }
    }
}