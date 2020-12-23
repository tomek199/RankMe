package com.tm.rankme.model.player

import java.util.*

data class Player(
    val id: UUID,
    val name: String,
    val deviation: Int,
    val rating: Int
)