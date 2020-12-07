package com.tm.rankme.storage.write.league

import com.eventstore.dbclient.RecordedEvent
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.tm.rankme.domain.base.Event
import com.tm.rankme.domain.league.League
import com.tm.rankme.domain.league.LeagueCreated
import com.tm.rankme.domain.league.LeagueRenamed
import com.tm.rankme.domain.league.LeagueSettingsChanged
import com.tm.rankme.storage.write.EsEventStorage
import com.tm.rankme.storage.write.EventStoreConnector
import com.tm.rankme.storage.write.InfrastructureException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository

@Repository
class LeagueEventStorage @Autowired constructor(
    eventStoreConnector: EventStoreConnector
) : EsEventStorage<League>(eventStoreConnector) {

    private val objectMapper: ObjectMapper = jacksonObjectMapper()

    override fun serialize(event: Event<League>): Any {
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

    override fun deserialize(recordedEvent: RecordedEvent): Event<League> {
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