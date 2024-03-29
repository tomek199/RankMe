package com.tm.rankme.domain.league

import com.tm.rankme.domain.base.Event

class LeagueSettingsChanged(
    aggregateId: String,
    version: Long,
    val allowDraws: Boolean,
    val maxScore: Int
) : Event<League>(aggregateId, version) {

    override val type: String = "league-settings-changed"

    override fun apply(aggregate: League) {
        aggregate.apply(this)
    }
}
