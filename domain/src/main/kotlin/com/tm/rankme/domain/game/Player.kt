package com.tm.rankme.domain.game

class Player(val competitorId: String, val username: String, var deviation: Int, var rating: Int) {
    var score: Int? = null
    var ratingDelta: Int? = null

    fun update(newDeviation: Int, newRating: Int) {
        deviation = newDeviation
        ratingDelta = newRating - rating
        rating = newRating
    }
}
