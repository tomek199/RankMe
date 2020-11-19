package com.tm.rankme.infrastructure.league

import com.eventstore.dbclient.ProposedEvent
import com.tm.rankme.domain.base.AggregateException
import com.tm.rankme.domain.base.Event
import com.tm.rankme.domain.league.League
import com.tm.rankme.domain.league.LeagueRepository
import com.tm.rankme.infrastructure.EventStoreConnector
import java.util.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository

@Repository
class EventSourceLeagueRepository @Autowired constructor(
    private val eventStoreConnector: EventStoreConnector
) : LeagueRepository {

    override fun byId(id: UUID): League {
        throw AggregateException("Not implemented yet")
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