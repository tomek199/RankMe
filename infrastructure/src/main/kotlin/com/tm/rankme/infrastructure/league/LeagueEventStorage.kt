package com.tm.rankme.infrastructure.league

import com.eventstore.dbclient.ProposedEvent
import com.eventstore.dbclient.RecordedEvent
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.tm.rankme.domain.base.Event
import com.tm.rankme.domain.league.League
import com.tm.rankme.domain.league.LeagueCreated
import com.tm.rankme.domain.league.LeagueRenamed
import com.tm.rankme.domain.league.LeagueSettingsChanged
import com.tm.rankme.infrastructure.EventStoreConnector
import com.tm.rankme.infrastructure.EventStorage
import com.tm.rankme.infrastructure.InfrastructureException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository

@Repository
class LeagueEventStorage @Autowired constructor(
    private val eventStoreConnector: EventStoreConnector
) : EventStorage<League> {

    private val objectMapper: ObjectMapper = jacksonObjectMapper()

    override fun save(event: Event<League>) {
        checkVersion(event)
        addToStream(event)
    }

    private fun checkVersion(event: Event<League>) {
        if (event.version != 0L) {
            val readResult = eventStoreConnector.stream.readStream(event.aggregateId.toString())
                .fromEnd().backward().execute(1).get()
            val currentVersion = readResult.events.getOrElse(0) {
                throw InfrastructureException("Cannon get actual version of league id=${event.aggregateId}")
            }.event.streamRevision
            if (currentVersion.valueUnsigned != event.version - 1)
                throw InfrastructureException("Version mismatch of league id=${event.aggregateId}")
        }
    }

    private fun addToStream(event: Event<League>) {
        val proposedEvent = ProposedEvent.builderAsJson(event.type, serialize(event)).build()
        eventStoreConnector.stream.appendStream(event.aggregateId.toString())
            .addEvent(proposedEvent).execute().get()
    }

    private fun serialize(event: Event<League>): Any {
        return when (event) {
            is LeagueCreated -> Created(
                event.type, event.aggregateId, event.version, event.timestamp,
                event.name, event.allowDraws, event.maxScore
            )
            is LeagueRenamed -> Renamed(
                event.type, event.aggregateId, event.version, event.timestamp,
                event.name
            )
            is LeagueSettingsChanged -> SettingsChanged(
                event.type, event.aggregateId, event.version, event.timestamp,
                event.allowDraws, event.maxScore
            )
            else -> throw InfrastructureException("Cannot serialize event '${event.type}'")
        }
    }

    override fun events(stream: String): List<Event<League>> {
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
            else -> throw InfrastructureException("Cannot deserialize event '${recordedEvent.eventType}'")
        }
    }
}