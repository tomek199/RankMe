package com.tm.rankme.infrastructure.game

import com.eventstore.dbclient.RecordedEvent
import com.tm.rankme.domain.base.Event
import com.tm.rankme.domain.game.Game
import com.tm.rankme.domain.game.GameRepository
import com.tm.rankme.infrastructure.EventStoreConnector
import com.tm.rankme.infrastructure.EventStoreRepository
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Repository

@Profile("eventstore")
@Repository
class EventStoreGameRepository(
    connector: EventStoreConnector,
    private val mapper: GameMapper
) : EventStoreRepository<Game>(connector), GameRepository {

    override fun byId(id: String): Game = events(id).let { Game.from(it) }

    override fun store(aggregate: Game) = aggregate.pendingEvents.forEach(this::save)

    override fun serialize(event: Event<Game>) = mapper.serialize(event)

    override fun deserialize(recordedEvent: RecordedEvent) = mapper.deserialize(recordedEvent.eventType, recordedEvent.eventData)
}