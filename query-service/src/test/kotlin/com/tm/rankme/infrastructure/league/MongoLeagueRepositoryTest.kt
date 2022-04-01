package com.tm.rankme.infrastructure.league

import com.aventrix.jnanoid.jnanoid.NanoIdUtils.randomNanoId
import com.tm.rankme.model.league.League
import com.tm.rankme.model.league.LeagueRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import java.util.*
import kotlin.random.Random
import kotlin.test.*

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
            assertNotNull(it.timestamp)
        }
    }

    @Test
    internal fun `Should return empty leagues page`() {
        // given
        every { accessor.getAllByOrderByTimestampAsc(ofType(Pageable::class)) } returns
                PageImpl(emptyList(), PageRequest.of(0, 3), 0)
        // when
        val page = repository.list(3)
        // then
        assertTrue(page.items.isEmpty())
        assertFalse(page.hasPreviousPage)
        assertFalse(page.hasNextPage)
    }

    @Test
    internal fun `Should return first leagues page`() {
        // given
        val leagues = List(4) {
            LeagueEntity(randomNanoId(), "League-${Random.nextInt()}", Random.nextBoolean(), Random.nextInt(10))
        }
        every { accessor.getAllByOrderByTimestampAsc(ofType(Pageable::class)) } returns
                PageImpl(leagues, PageRequest.of(0, 4), 12)
        // when
        val page = repository.list(4)
        // then
        assertEquals(4, page.items.size)
        assertEquals(leagues.first().id, page.items.first().node.id)
        assertEquals(leagues.last().id, page.items.last().node.id)
        assertFalse(page.hasPreviousPage)
        assertTrue(page.hasNextPage)
    }

    @Test
    internal fun `Should return middle leagues page after given cursor`() {
        // given
        val leagues = List(6) {
            LeagueEntity(randomNanoId(), "League-${Random.nextInt()}", Random.nextBoolean(), Random.nextInt(10))
        }
        every { accessor.getByTimestampGreaterThanOrderByTimestampAsc(leagues.first().timestamp, ofType(Pageable::class)) } returns
                PageImpl(leagues.subList(1, 6), PageRequest.of(0, 5), 11)
        val afterCursor = Base64.getEncoder().encodeToString(leagues.first().timestamp.toString().toByteArray())
        // when
        val page = repository.listAfter(4, afterCursor)
        // then
        assertEquals(5, page.items.size)
        assertEquals(leagues[1].id, page.items.first().node.id)
        assertEquals(leagues.last().id, page.items.last().node.id)
        assertTrue(page.hasPreviousPage)
        assertTrue(page.hasNextPage)
    }

    @Test
    internal fun `Should return last leagues page after given cursor`() {
        // given
        val leagues = List(9) {
            LeagueEntity(randomNanoId(), "League-${Random.nextInt()}", Random.nextBoolean(), Random.nextInt(10))
        }
        every { accessor.getByTimestampGreaterThanOrderByTimestampAsc(leagues.first().timestamp, ofType(Pageable::class)) } returns
                PageImpl(leagues.takeLast(8), PageRequest.of(0, 8), 8)
        val afterCursor = Base64.getEncoder().encodeToString(leagues.first().timestamp.toString().toByteArray())
        // when
        val page = repository.listAfter(8, afterCursor)
        // then
        assertEquals(8, page.items.size)
        assertEquals(leagues[1].id, page.items.first().node.id)
        assertEquals(leagues.last().id, page.items.last().node.id)
        assertTrue(page.hasPreviousPage)
        assertFalse(page.hasNextPage)
    }

    @Test
    internal fun `Should return middle leagues page before given cursor`() {
        // given
        val leagues = List(5) {
            LeagueEntity(randomNanoId(), "League-${Random.nextInt()}", Random.nextBoolean(), Random.nextInt(10))
        }
        every { accessor.getByTimestampLessThanOrderByTimestampDesc(leagues.last().timestamp, ofType(Pageable::class)) } returns
                PageImpl(leagues.subList(0, 4).reversed(), PageRequest.of(0, 4), 12)
        val beforeCursor = Base64.getEncoder().encodeToString(leagues.last().timestamp.toString().toByteArray())
        // when
        val page = repository.listBefore(4, beforeCursor)
        // then
        assertEquals(4, page.items.size)
        assertEquals(leagues.first().id, page.items.first().node.id)
        assertEquals(leagues[3].id, page.items.last().node.id)
        assertTrue(page.hasPreviousPage)
        assertTrue(page.hasNextPage)
    }

    @Test
    internal fun `Should return first leagues page before given cursor`() {
        // given
        val leagues = List(8) {
            LeagueEntity(randomNanoId(), "League-${Random.nextInt()}", Random.nextBoolean(), Random.nextInt(10))
        }
        every { accessor.getByTimestampLessThanOrderByTimestampDesc(leagues.last().timestamp, ofType(Pageable::class)) } returns
                PageImpl(leagues.subList(0, 7).reversed(), PageRequest.of(0, 7), 7)
        val beforeCursor = Base64.getEncoder().encodeToString(leagues.last().timestamp.toString().toByteArray())
        // when
        val page = repository.listBefore(7, beforeCursor)
        // then
        assertEquals(7, page.items.size)
        assertEquals(leagues.first().id, page.items.first().node.id)
        assertEquals(leagues[6].id, page.items.last().node.id)
        assertFalse(page.hasPreviousPage)
        assertTrue(page.hasNextPage)
    }
}