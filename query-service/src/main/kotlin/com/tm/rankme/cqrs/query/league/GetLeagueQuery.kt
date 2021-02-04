package com.tm.rankme.cqrs.query.league

import com.tm.rankme.cqrs.query.Query
import java.util.*

data class GetLeagueQuery(val id: UUID) : Query()
