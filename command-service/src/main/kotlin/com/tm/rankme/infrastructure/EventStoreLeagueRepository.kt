package com.tm.rankme.infrastructure

import com.eventstore.dbclient.RecordedEvent
import com.tm.rankme.domain.base.Event
import com.tm.rankme.domain.league.League
import com.tm.rankme.domain.league.LeagueCreated
import com.tm.rankme.domain.league.LeagueRenamed
import com.tm.rankme.domain.league.LeagueRepository
import com.tm.rankme.domain.league.LeagueSettingsChanged
import java.util.*
import org.springframework.stereotype.Repository

@Repository
class EventStoreLeagueRepository(
    connector: EventStoreConnector
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
            "league-created" -> objectMapper.readValue(recordedEvent.eventData, Created::class.java).let {
                LeagueCreated(it.name, it.allowDraws, it.maxScore, it.aggregateId)
            }
            "league-renamed" -> objectMapper.readValue(recordedEvent.eventData, Renamed::class.java).let {
                LeagueRenamed(it.aggregateId, it.version, it.name)
            }
            "league-settings-changed" -> objectMapper.readValue(recordedEvent.eventData, SettingsChanged::class.java).let {
                LeagueSettingsChanged(it.aggregateId, it.version, it.allowDraws, it.maxScore)
            }
            else -> throw InfrastructureException("Cannot deserialize event '${recordedEvent.eventType}'")
        }
    }

    internal data class Created(
        val type: String,
        val aggregateId: UUID,
        val version: Long,
        val timestamp: Long,
        val name: String,
        val allowDraws: Boolean = false,
        val maxScore: Int = 2
    )

    internal data class Renamed(
        val type: String,
        val aggregateId: UUID,
        val version: Long,
        val timestamp: Long,
        val name: String
    )

    internal data class SettingsChanged(
        val type: String,
        val aggregateId: UUID,
        val version: Long,
        val timestamp: Long,
        val allowDraws: Boolean = false,
        val maxScore: Int = 2
    )
}