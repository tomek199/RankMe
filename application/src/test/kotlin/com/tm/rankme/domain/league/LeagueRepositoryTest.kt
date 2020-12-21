package com.tm.rankme.domain.league

import com.tm.rankme.domain.base.EventEmitter
import com.tm.rankme.domain.base.EventStorage
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifySequence
import java.util.*
import org.junit.jupiter.api.Test

internal class LeagueRepositoryTest {
    private val eventStorage = mockk<EventStorage<League>>(relaxed = true)
    private val eventEmitter = mockk<EventEmitter>(relaxed = true)
    private val repository = object : LeagueRepository(eventStorage, eventEmitter) { }

    @Test
    internal fun `Should get league by id using event storage`() {
        // given
        val aggregateId = UUID.randomUUID()
        val created = LeagueCreated("Star Wars", aggregateId = aggregateId)
        every { eventStorage.events(aggregateId.toString()) } returns listOf(created)
        // when
        val league = repository.byId(aggregateId)
        // then
        verify(exactly = 1) { eventStorage.events(aggregateId.toString()) }
    }

    @Test
    internal fun `Should store event using event storage and event emitter`() {
        // given
        val league = League.create("Star Wars")
        // when
        repository.store(league)
        // then
        verifySequence {
            eventStorage.save(ofType(LeagueCreated::class))
            eventEmitter.emit(ofType(LeagueCreated::class))
        }
    }
}