package com.tm.rankme.infrastructure.league

import com.eventstore.dbclient.RecordedEvent
import com.tm.rankme.domain.base.Event
import com.tm.rankme.domain.league.League
import com.tm.rankme.domain.league.LeagueRepository
import com.tm.rankme.infrastructure.EventStoreConnector
import com.tm.rankme.infrastructure.EventStoreRepository
import com.tm.rankme.infrastructure.InfrastructureException
import java.util.*
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Repository

@Profile("eventstore")
@Repository
class EventStoreLeagueRepository(
    connector: EventStoreConnector,
    private val mapper: LeagueMapper
) : EventStoreRepository<League>(connector), LeagueRepository {

    override fun byId(id: UUID): League = events(id.toString()).let { League.from(it) }

    override fun store(aggregate: League) = aggregate.pendingEvents.forEach(this::save)

    override fun exist(id: UUID): Boolean {
        return try {
            events(id.toString()).isNotEmpty()
        } catch (e: InfrastructureException) {
            false
        }
    }

    override fun serialize(event: Event<League>) = mapper.serialize(event)

    override fun deserialize(recordedEvent: RecordedEvent) = mapper.deserialize(recordedEvent.eventType, recordedEvent.eventData)
}