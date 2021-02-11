package com.tm.rankme.infrastructure.player

import com.eventstore.dbclient.RecordedEvent
import com.tm.rankme.domain.base.Event
import com.tm.rankme.domain.player.Player
import com.tm.rankme.domain.player.PlayerRepository
import com.tm.rankme.infrastructure.EventStoreConnector
import com.tm.rankme.infrastructure.EventStoreRepository
import java.util.*
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Repository

@Profile("eventstore")
@Repository
class EventStorePlayerRepository(
    connector: EventStoreConnector,
    private val mapper: PlayerMapper
) : EventStoreRepository<Player>(connector), PlayerRepository {

    override fun byId(id: UUID): Player = events(id.toString()).let { Player.from(it) }

    override fun store(aggregate: Player) = aggregate.pendingEvents.forEach(this::save)

    override fun serialize(event: Event<Player>) = mapper.serialize(event)

    override fun deserialize(recordedEvent: RecordedEvent) = mapper.deserialize(recordedEvent.eventType, recordedEvent.eventData)
}