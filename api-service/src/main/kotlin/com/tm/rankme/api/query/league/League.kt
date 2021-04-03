package com.tm.rankme.api.query.league

import java.util.*

data class League(
    val id: UUID,
    val name: String,
    val allowDraws: Boolean,
    val maxScore: Int
)