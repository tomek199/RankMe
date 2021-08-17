package com.tm.rankme.model.player

data class Player(
    val id: String,
    val leagueId: String,
    val name: String,
    var deviation: Int,
    var rating: Int
)