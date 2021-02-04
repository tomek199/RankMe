package com.tm.rankme.domain.league

import com.tm.rankme.domain.base.AggregateRoot
import com.tm.rankme.domain.base.Event

class League private constructor() : AggregateRoot() {
    val pendingEvents = mutableListOf<Event<League>>()
    lateinit var name: String
        private set
    val settings: Settings = Settings()

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

    fun rename(name: String) {
        add(LeagueRenamed(id, ++version, name))
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

    internal fun apply(event: LeagueRenamed) {
        name = event.name
    }

    internal fun apply(event: LeagueSettingsChanged) {
        settings.allowDraws = event.allowDraws
        settings.maxScore = event.maxScore
    }
}
