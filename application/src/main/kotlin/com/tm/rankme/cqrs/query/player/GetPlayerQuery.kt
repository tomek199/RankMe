package com.tm.rankme.cqrs.query.player

import com.tm.rankme.cqrs.query.Query
import java.util.*

data class GetPlayerQuery(val id: UUID) : Query()
