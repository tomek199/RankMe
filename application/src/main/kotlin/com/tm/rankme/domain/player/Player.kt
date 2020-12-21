package com.tm.rankme.domain.player

import com.tm.rankme.domain.base.AggregateRoot
import com.tm.rankme.domain.base.Event
import java.util.*

class Player private constructor() : AggregateRoot() {
    val pendingEvents = mutableListOf<Event<Player>>()
    lateinit var leagueId: UUID
        private set
    lateinit var name: String
        private set
    var deviation: Int = 350
        private set
    var rating: Int = 1500
        private set

    companion object {
        fun create(leagueId: UUID, name: String): Player {
            val event = PlayerCreated(leagueId, name)
            val player = Player()
            player.add(event)
            return player
        }

        fun from(events: List<Event<Player>>): Player {
            val player = Player()
            events.forEach { event -> event.apply(player) }
            player.version = events.last().version
            return player
        }
    }

    private fun add(event: Event<Player>) {
        pendingEvents.add(event)
        version = event.version
        event.apply(this)
    }

    internal fun apply(event: PlayerCreated) {
        id = event.aggregateId
        leagueId = event.leagueId
        name = event.name
        deviation = event.deviation
        rating = event.rating
    }
}