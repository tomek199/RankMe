package com.tm.rankme.api.query.player

import java.util.*

data class Player(
    val id: UUID,
    val name: String,
    val deviation: Int,
    val rating: Int
)