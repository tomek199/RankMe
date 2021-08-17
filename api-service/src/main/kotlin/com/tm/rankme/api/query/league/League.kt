package com.tm.rankme.api.query.league

data class League(
    val id: String,
    val name: String,
    val allowDraws: Boolean,
    val maxScore: Int
)