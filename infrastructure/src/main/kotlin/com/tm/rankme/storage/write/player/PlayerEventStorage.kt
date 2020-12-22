package com.tm.rankme.storage.write.player

import com.eventstore.dbclient.RecordedEvent
import com.tm.rankme.domain.base.Event
import com.tm.rankme.domain.player.Player
import com.tm.rankme.domain.player.PlayerCreated
import com.tm.rankme.storage.write.EsEventStorage
import com.tm.rankme.storage.write.EventStoreConnector
import com.tm.rankme.storage.write.InfrastructureException
import java.util.*
import org.springframework.stereotype.Repository

@Repository
class PlayerEventStorage(
    connector: EventStoreConnector
) : EsEventStorage<Player>(connector) {

    override fun serialize(event: Event<Player>): Any {
        return when (event) {
            is PlayerCreated -> Created(
                event.type, event.aggregateId, event.version, event.timestamp,
                event.leagueId, event.name, event.deviation, event.rating
            )
            else -> throw InfrastructureException("Cannot serialize event '${event.type}'")
        }
    }

    override fun deserialize(recordedEvent: RecordedEvent): Event<Player> {
        return when (recordedEvent.eventType) {
            "player-created" -> objectMapper.readValue(recordedEvent.eventData, Created::class.java).let {
                PlayerCreated(it.leagueId, it.name, it.deviation, it.rating, it.aggregateId)
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
}