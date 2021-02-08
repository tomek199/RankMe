package com.tm.rankme.infrastructure.player

import com.eventstore.dbclient.RecordedEvent
import com.tm.rankme.domain.base.Event
import com.tm.rankme.domain.player.Player
import com.tm.rankme.domain.player.PlayerCreated
import com.tm.rankme.domain.player.PlayerPlayedGame
import com.tm.rankme.domain.player.PlayerRepository
import com.tm.rankme.infrastructure.EventStoreConnector
import com.tm.rankme.infrastructure.EventStoreRepository
import com.tm.rankme.infrastructure.InfrastructureException
import java.util.*
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Repository

@Profile("eventstore")
@Repository
class EventStorePlayerRepository(
    connector: EventStoreConnector
) : EventStoreRepository<Player>(connector), PlayerRepository {

    override fun byId(id: UUID): Player = events(id.toString()).let { Player.from(it) }

    override fun store(aggregate: Player) = aggregate.pendingEvents.forEach(this::save)

    override fun serialize(event: Event<Player>): Any {
        return when (event) {
            is PlayerCreated -> Created(
                event.type, event.aggregateId, event.version, event.timestamp,
                event.leagueId, event.name, event.deviation, event.rating
            )
            is PlayerPlayedGame -> PlayedGame(
                event.type, event.aggregateId, event.version, event.timestamp,
                event.deviationDelta, event.ratingDelta, event.score
            )
            else -> throw InfrastructureException("Cannot serialize event '${event.type}'")
        }
    }

    override fun deserialize(recordedEvent: RecordedEvent): Event<Player> {
        return when (recordedEvent.eventType) {
            "player-created" -> objectMapper.readValue(recordedEvent.eventData, Created::class.java).let {
                PlayerCreated(it.leagueId, it.name, it.deviation, it.rating, it.aggregateId)
            }
            "player-played-game" -> objectMapper.readValue(recordedEvent.eventData, PlayedGame::class.java).let {
                PlayerPlayedGame(it.deviationDelta, it.ratingDelta, it.score, it.aggregateId, it.version)
            }
            else -> throw InfrastructureException("Cannot deserialize event '${recordedEvent.eventType}'")
        }
    }

    internal data class Created(
        val type: String,
        val aggregateId: UUID,
        val version: Long,
        val timestamp: Long,
        val leagueId: UUID,
        val name: String,
        val deviation: Int,
        val rating: Int
    )

    internal data class PlayedGame(
        val type: String,
        val aggregateId: UUID,
        val version: Long,
        val timestamp: Long,
        val deviationDelta: Int,
        val ratingDelta: Int,
        val score: Int
    )
}