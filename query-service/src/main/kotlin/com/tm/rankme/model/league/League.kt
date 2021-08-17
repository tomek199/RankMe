package com.tm.rankme.model.league

data class League(
    val id: String,
    var name: String,
    var allowDraws: Boolean,
    var maxScore: Int
)
