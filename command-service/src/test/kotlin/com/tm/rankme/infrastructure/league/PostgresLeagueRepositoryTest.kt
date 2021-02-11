package com.tm.rankme.infrastructure.league

import com.tm.rankme.domain.league.League
import com.tm.rankme.domain.league.LeagueCreated
import com.tm.rankme.domain.league.LeagueRenamed
import com.tm.rankme.domain.league.LeagueSettingsChanged
import com.tm.rankme.infrastructure.InfrastructureException
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import io.mockk.verifySequence
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test

internal class PostgresLeagueRepositoryTest {
    private val accessor = mockk<LeagueAccessor>()
    private val mapper = mockk<LeagueMapper>()
    private val repository = PostgresLeagueRepository(accessor, mapper)

    @Test
    internal fun `Should save 'league-created' event with initial version 0`() {
        // given
        val league = League.create("Star Wars")
        every { mapper.serialize(ofType(LeagueCreated::class)) } returns String()
        every { accessor.save(ofType(LeagueEntity::class)) } returns mockk()
        // when
        repository.store(league)
        // then
        val entitySlot = slot<LeagueEntity>()
        verify(exactly = 0) { accessor.getFirstByAggregateIdOrderByTimestampDesc(league.id) }
        verifySequence {
            mapper.serialize(ofType(LeagueCreated::class))
            accessor.save(capture(entitySlot))
        }
        entitySlot.captured.let {
            assertEquals(league.id, it.aggregateId)
            assertEquals("league-created", it.type)
            assertEquals(0, it.version)
            assertNotNull(it.payload)
        }
    }

    @Test
    internal fun `Should save 'league-renamed' event with version 1`() {
        // given
        val league = League.from(listOf(LeagueCreated("Star Wars")))
        league.rename("Transformers")
        every { mapper.serialize(ofType(LeagueRenamed::class)) } returns String()
        every { accessor.getFirstByAggregateIdOrderByTimestampDesc(league.id) } returns
            LeagueEntity(league.id, "league-created", 0, 12345, "{}", 1)
        every { accessor.save(ofType(LeagueEntity::class)) } returns mockk()
        // when
        repository.store(league)
        // then
        val entitySlot = slot<LeagueEntity>()
        verifySequence {
            accessor.getFirstByAggregateIdOrderByTimestampDesc(league.id)
            mapper.serialize(ofType(LeagueRenamed::class))
            accessor.save(capture(entitySlot))
        }
        entitySlot.captured.let {
            assertEquals(league.id, it.aggregateId)
            assertEquals("league-renamed", it.type)
            assertEquals(1, it.version)
            assertNotNull(it.payload)
        }
    }

    @Test
    internal fun `Should save 'league-settings-changed' event with version 2`() {
        // given
        val leagueId = UUID.randomUUID()
        val league = League.from(listOf(
            LeagueCreated("Star Wars", aggregateId = leagueId),
            LeagueRenamed(leagueId, 1, "Transformers")
        ))
        league.settings(true, 5)
        every { mapper.serialize(ofType(LeagueSettingsChanged::class)) } returns String()
        every { accessor.getFirstByAggregateIdOrderByTimestampDesc(league.id) } returns
            LeagueEntity(league.id, "league-renamed", 1, 12345, "{}", 2)
        every { accessor.save(ofType(LeagueEntity::class)) } returns mockk()
        // when
        repository.store(league)
        // then
        val entitySlot = slot<LeagueEntity>()
        verifySequence {
            accessor.getFirstByAggregateIdOrderByTimestampDesc(league.id)
            mapper.serialize(ofType(LeagueSettingsChanged::class))
            accessor.save(capture(entitySlot))
        }
        entitySlot.captured.let {
            assertEquals(league.id, it.aggregateId)
            assertEquals("league-settings-changed", it.type)
            assertEquals(2, it.version)
            assertNotNull(it.payload)
        }
    }

    @Test
    internal fun `Should throw exception when cannot get actual aggregate version`() {
        // given
        val league = League.from(listOf(LeagueCreated("Star Wars")))
        league.rename("Transformers")
        every { accessor.getFirstByAggregateIdOrderByTimestampDesc(league.id) } returns null
        // when
        val exception = assertFailsWith<InfrastructureException> { repository.store(league) }
        // then
        assertEquals("Cannon get actual version of aggregate id=${league.id}", exception.message)
    }

    @Test
    internal fun `Should throw exception when event version is out of date`() {
        // given
        val league = League.from(listOf(LeagueCreated("Star Wars")))
        league.rename("Transformers")
        every { accessor.getFirstByAggregateIdOrderByTimestampDesc(league.id) } returns
            LeagueEntity(league.id, "league-renamed", 1, 12345, "{}", 2)
        // when
        val exception = assertFailsWith<InfrastructureException> { repository.store(league) }
        // then
        assertEquals("Version mismatch of aggregate id=${league.id}", exception.message)
    }

    @Test
    internal fun `Should return aggregate from events`() {
        // given
        val aggregateId = UUID.randomUUID()
        every { accessor.getByAggregateIdOrderByTimestampAsc(aggregateId) } returns listOf(
            LeagueEntity(aggregateId, "league-created", 0, 11111, "{}", 1),
            LeagueEntity(aggregateId, "league-renamed", 1, 22222, "{}", 2),
            LeagueEntity(aggregateId, "league-settings-changed", 2, 33333, "{}", 3)
        )
        every { mapper.deserialize(ofType(String::class), ofType(String::class)) } returnsMany listOf(
            LeagueCreated("Star Wars", false, 2, aggregateId),
            LeagueRenamed(aggregateId, 1, "Transformers"),
            LeagueSettingsChanged(aggregateId, 2, true, 10)
        )
        // when
        val league = repository.byId(aggregateId)
        // then
        assertEquals(aggregateId, league.id)
        assertEquals(2, league.version)
        assertTrue(league.pendingEvents.isEmpty())
        assertEquals("Transformers", league.name)
        assertTrue(league.settings.allowDraws)
        assertEquals(10, league.settings.maxScore)
    }

//    @Test
//    internal fun `Should do something`() {
//        // given
//        val aggregateId = UUID.randomUUID()
//        every { accessor.getByAggregateIdOrderByTimestampAsc(aggregateId) } returns listOf(
////            LeagueEntity(aggregateId, "league-created", 0, 11111, "", 1),
//        )
//        every { mapper.deserialize(any(), any()) } throws InfrastructureException("")
//        // when
//        assertFailsWith<InfrastructureException> { repository.byId(aggregateId) }
//        // then
//    }

    @Test
    internal fun `Should throw exception when events for aggregate are not found`() {
        // given
        val aggregateId = UUID.randomUUID()
        every { accessor.getByAggregateIdOrderByTimestampAsc(aggregateId) } returns emptyList()
        // when
        val exception = assertFailsWith<InfrastructureException> { repository.byId(aggregateId) }
        // then
        assertEquals("Stream $aggregateId is not found", exception.message)
    }

    @Test
    internal fun `Should return true when league exist`() {
        // given
        val leagueId = UUID.randomUUID()
        every { accessor.getFirstByAggregateIdOrderByTimestampDesc(leagueId) } returns
            LeagueEntity(leagueId, "league-created", 0, 12345, "{}", 1)
        // then
        assertTrue(repository.exist(leagueId))
    }

    @Test
    internal fun `Should return false when league does not exist`() {
        // given
        val leagueId = UUID.randomUUID()
        every { accessor.getFirstByAggregateIdOrderByTimestampDesc(leagueId) } returns null
        // then
        assertFalse(repository.exist(leagueId))
    }
}