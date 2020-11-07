package com.tm.rankme.domain.league

import com.tm.rankme.domain.base.Event
import java.util.*


class LeagueRenamed(
    aggregateId: UUID,
    version: Int,
    val name: String
) : Event<League>(aggregateId, version) {

    override val type: String = "league-renamed"

    override fun apply(aggregate: League) {
        aggregate.apply(this)
    }
}