package com.tm.rankme.domain.game

class Player(val competitorId: String, val username: String, var deviation: Int, var rating: Int) {
    var score: Int? = null
        internal set
    var ratingDelta: Int? = null
        private set

    fun update(newDeviation: Int, newRating: Int) {
        deviation = newDeviation
        ratingDelta = newRating - rating
        rating = newRating
    }
}
