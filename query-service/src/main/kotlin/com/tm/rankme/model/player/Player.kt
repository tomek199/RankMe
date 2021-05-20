package com.tm.rankme.model.player

import java.util.*

data class Player(
    val id: UUID,
    val leagueId: UUID,
    val name: String,
    var deviation: Int,
    var rating: Int
)