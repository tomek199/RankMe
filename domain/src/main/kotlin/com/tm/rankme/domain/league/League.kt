package com.tm.rankme.domain.league

import com.tm.rankme.domain.base.Event
import java.util.*

class League private constructor(
    val settings: Settings = Settings(),
    val pendingEvents: MutableList<Event<League>> = mutableListOf()
) {
    lateinit var id: UUID
        private set
    var version: Int = 0
        private set
    lateinit var name: String
        private set

    companion object {
        fun create(name: String): League {
            val event = LeagueCreated(name)
            val league = League()
            league.add(event)
            return league
        }

        fun from(events: List<Event<League>>): League {
            val league = League()
            events.forEach { event -> event.apply(league) }
            league.version = events.last().version
            return league
        }
    }

    fun name(name: String) {
        add(LeagueNameChanged(id, ++version, name))
    }

    fun settings(allowDraws: Boolean, maxScore: Int) {
        add(LeagueSettingsChanged(id, ++version, allowDraws, maxScore))
    }

    private fun add(event: Event<League>) {
        pendingEvents.add(event)
        version = event.version
        event.apply(this)
    }

    internal fun apply(event: LeagueCreated) {
        id = event.aggregateId
        name = event.name
        settings.allowDraws = event.allowDraws
        settings.maxScore = event.maxScore
    }

    internal fun apply(event: LeagueNameChanged) {
        name = event.name
    }

    internal fun apply(event: LeagueSettingsChanged) {
        settings.allowDraws = event.allowDraws
        settings.maxScore = event.maxScore
    }
}
