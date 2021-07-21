package com.tm.rankme.domain.game

data class Result(
    val score: Int,
    val deviation: Int,
    val deviationDelta: Int,
    val rating: Int,
    val ratingDelta: Int
)
