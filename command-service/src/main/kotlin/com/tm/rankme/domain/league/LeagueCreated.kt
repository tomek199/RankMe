package com.tm.rankme.domain.league

import com.aventrix.jnanoid.jnanoid.NanoIdUtils.randomNanoId
import com.tm.rankme.domain.base.Event

class LeagueCreated(
    val name: String,
    val allowDraws: Boolean = false,
    val maxScore: Int = 2,
    aggregateId: String = randomNanoId()
) : Event<League>(aggregateId, 0) {

    override val type: String = "league-created"

    override fun apply(aggregate: League) {
        aggregate.apply(this)
    }
}