package com.tm.rankme.domain.game

class Player(
    val competitorId: String, val username: String,
    var deviation: Int, var rating: Int, val score: Int, val ratingDelta: Int
)
