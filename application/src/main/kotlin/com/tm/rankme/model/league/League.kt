package com.tm.rankme.model.league

import java.util.*

data class League(
    val id: UUID,
    val name: String,
    val allowDraws: Boolean,
    val maxScore: Int
)
