package com.tm.rankme.e2e.query

import java.util.*

data class Player(
    val id: UUID,
    val name: String,
    val deviation: Int,
    val rating: Int
)

data class League(
    val id: UUID,
    val name: String,
    val allowDraws: Boolean,
    val maxScore: Int,
    val players: List<Player>
)