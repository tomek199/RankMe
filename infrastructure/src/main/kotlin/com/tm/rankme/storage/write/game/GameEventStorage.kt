package com.tm.rankme.storage.write.game

import com.eventstore.dbclient.RecordedEvent
import com.tm.rankme.domain.base.Event
import com.tm.rankme.domain.game.Game
import com.tm.rankme.domain.game.GamePlayed
import com.tm.rankme.storage.write.EsEventStorage
import com.tm.rankme.storage.write.EventStoreConnector
import com.tm.rankme.storage.write.InfrastructureException
import java.util.*
import org.springframework.stereotype.Repository

@Repository
class GameEventStorage(
    connection: EventStoreConnector
) : EsEventStorage<Game>(connection) {

    override fun serialize(event: Event<Game>): Any {
        return when (event) {
            is GamePlayed -> Played(
                event.type, event.aggregateId, event.version, event.timestamp, event.leagueId,
                event.firstId, event.firstScore, event.firstDeviationDelta, event.firstRatingDelta,
                event.secondId, event.secondScore, event.secondDeviationDelta, event.secondRatingDelta
            )
            else -> throw InfrastructureException("Cannot serialize event '${event.type}'")
        }
    }

    override fun deserialize(recordedEvent: RecordedEvent): Event<Game> {
        return when (recordedEvent.eventType) {
            "game-played" -> objectMapper.readValue(recordedEvent.eventData, Played::class.java).let {
                GamePlayed(it.leagueId, it.firstId, it.firstScore, it.firstDeviationDelta, it.firstRatingDelta,
                it.secondId, it.secondScore, it.secondDeviationDelta, it.secondRatingDelta, it.aggregateId)
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
        val firstId: UUID,
        val firstScore: Int,
        val firstDeviationDelta: Int,
        val firstRatingDelta: Int,
        val secondId: UUID,
        val secondScore: Int,
        val secondDeviationDelta: Int,
        val secondRatingDelta: Int
    )
}