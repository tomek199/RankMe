package com.tm.rankme.domain.league

import com.tm.rankme.domain.base.Event
import java.util.*


class LeagueNameChanged(
    aggregateId: UUID,
    version: Int,
    val name: String
) : Event<League>(aggregateId, version) {

    override val type: String = "league-name-changed"

    override fun apply(aggregate: League) {
        aggregate.apply(this)
    }
}