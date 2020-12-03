package com.tm.rankme.domain.league

import com.tm.rankme.domain.base.Event
import java.util.*

class LeagueCreated(
    val name: String,
    val allowDraws: Boolean = false,
    val maxScore: Int = 2,
    aggregateId: UUID = UUID.randomUUID()
) : Event<League>(aggregateId, 0)
{
    override val type: String = "league-created"

    override fun apply(aggregate: League) {
        aggregate.apply(this)
    }
}