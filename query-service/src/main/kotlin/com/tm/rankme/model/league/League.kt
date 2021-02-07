package com.tm.rankme.model.league

import java.util.*

data class League(
    val id: UUID,
    var name: String,
    var allowDraws: Boolean,
    var maxScore: Int
)
