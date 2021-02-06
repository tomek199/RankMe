package com.tm.rankme.infrastructure

import com.eventstore.dbclient.RecordedEvent
import com.tm.rankme.domain.base.Event
import com.tm.rankme.domain.game.Game
import com.tm.rankme.domain.game.GamePlayed
import com.tm.rankme.domain.game.GameRepository
import com.tm.rankme.domain.game.GameScheduled
import java.util.*
import org.springframework.stereotype.Repository

@Repository
class EventStoreGameRepository(
    connector: EventStoreConnector
) : com.tm.rankme.infrastructure.EventStoreRepository<Game>(connector), GameRepository {

    override fun byId(id: UUID): Game = events(id.toString()).let { Game.from(it) }

    override fun store(aggregate: Game) = aggregate.pendingEvents.forEach(this::save)

    override fun serialize(event: Event<Game>): Any {
        return when (event) {
            is GamePlayed -> Played(
                event.type, event.aggregateId, event.version, event.timestamp, event.leagueId, event.dateTime,
                event.firstId, event.firstScore, event.firstDeviationDelta, event.firstRatingDelta,
                event.secondId, event.secondScore, event.secondDeviationDelta, event.secondRatingDelta
            )
            is GameScheduled -> Scheduled(
                event.type, event.aggregateId, event.version, event.timestamp, event.leagueId,
                event.dateTime, event.firstId, event.secondId
            )
            else -> throw InfrastructureException("Cannot serialize event '${event.type}'")
        }
    }

    override fun deserialize(recordedEvent: RecordedEvent): Event<Game> {
        return when (recordedEvent.eventType) {
            "game-scheduled" -> objectMapper.readValue(recordedEvent.eventData, Scheduled::class.java).let {
                GameScheduled(it.leagueId, it.firstId, it.secondId, it.dateTime, it.aggregateId)
            }
            "game-played" -> objectMapper.readValue(recordedEvent.eventData, Played::class.java).let {
                GamePlayed(
                    it.leagueId, it.firstId, it.firstScore, it.firstDeviationDelta, it.firstRatingDelta,
                    it.secondId, it.secondScore, it.secondDeviationDelta, it.secondRatingDelta,
                    it.dateTime, it.aggregateId, it.version
                )
            }
            else -> throw InfrastructureException("Cannot deserialize event '${recordedEvent.eventType}'")
        }
    }

    internal data class Played(
        val type: String,
        val aggregateId: UUID,
        val version: Long,
        val timestamp: Long,
        val leagueId: UUID,
        val dateTime: Long,
        val firstId: UUID,
        val firstScore: Int,
        val firstDeviationDelta: Int,
        val firstRatingDelta: Int,
        val secondId: UUID,
        val secondScore: Int,
        val secondDeviationDelta: Int,
        val secondRatingDelta: Int
    )

    internal data class Scheduled(
        val type: String,
        val aggregateId: UUID,
        val version: Long,
        val timestamp: Long,
        val leagueId: UUID,
        val dateTime: Long,
        val firstId: UUID,
        val secondId: UUID
    )
}