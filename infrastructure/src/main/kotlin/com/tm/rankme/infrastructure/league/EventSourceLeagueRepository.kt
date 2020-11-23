package com.tm.rankme.infrastructure.league

import com.eventstore.dbclient.ProposedEvent
import com.eventstore.dbclient.RecordedEvent
import com.eventstore.dbclient.ResolvedEvent
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.tm.rankme.domain.base.AggregateException
import com.tm.rankme.domain.base.Event
import com.tm.rankme.domain.league.League
import com.tm.rankme.domain.league.LeagueCreated
import com.tm.rankme.domain.league.LeagueRenamed
import com.tm.rankme.domain.league.LeagueRepository
import com.tm.rankme.domain.league.LeagueSettingsChanged
import com.tm.rankme.infrastructure.EventStoreConnector
import java.util.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository

@Repository
class EventSourceLeagueRepository @Autowired constructor(
    private val eventStoreConnector: EventStoreConnector
) : LeagueRepository {

    private val objectMapper: ObjectMapper = jacksonObjectMapper()

    override fun byId(id: UUID): League {
        val events = streamEvents(id.toString())
        return League.from(events)
    }

    private fun streamEvents(stream: String): List<Event<League>> {
        return eventStoreConnector.stream.readStream(stream)
            .fromStart().readThrough().get().events.map { deserialize(it.originalEvent) }.toList()
    }

    private fun deserialize(recordedEvent: RecordedEvent): Event<League> {
        return when (recordedEvent.eventType) {
            "league-created" -> {
                val event = objectMapper.readValue(recordedEvent.eventData, Created::class.java)
                LeagueCreated(event.name, event.allowDraws, event.maxScore, event.aggregateId)
            }
            "league-renamed" -> {
                val event = objectMapper.readValue(recordedEvent.eventData, Renamed::class.java)
                LeagueRenamed(event.aggregateId, event.version, event.name)
            }
            "league-settings-changed" -> {
                val event = objectMapper.readValue(recordedEvent.eventData, SettingsChanged::class.java)
                LeagueSettingsChanged(event.aggregateId, event.version, event.allowDraws, event.maxScore)
            }
            else -> throw AggregateException("Event is not known")
        }
    }

    override fun store(aggregate: League) = aggregate.pendingEvents.forEach {
        checkVersion(it)
        addToStream(it)
    }

    private fun checkVersion(event: Event<League>) {
        if (event.version != 0L) {
            val readResult = eventStoreConnector.stream.readStream(event.aggregateId.toString())
                .fromEnd().backward().execute(1).get()
            val currentVersion = readResult.events.getOrElse(0) {
                throw AggregateException("Cannon get actual version of league id=${event.aggregateId}")
            }.event.streamRevision
            if (currentVersion.valueUnsigned != event.version - 1)
                throw AggregateException("Version mismatch of league id=${event.aggregateId}")
        }
    }

    private fun addToStream(event: Event<League>) {
        val proposedEvent = ProposedEvent.builderAsJson(event.type, event).build()
        eventStoreConnector.stream.appendStream(event.aggregateId.toString())
            .addEvent(proposedEvent).execute().get()
    }
}