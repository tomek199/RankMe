package com.tm.rankme.domain.game

class Player(
    val competitorId: String, val username: String,
    deviation: Int, rating: Int, var result: Result? = null
) {
    var deviation: Int = deviation
        private set
        get() {
            if (result == null) return field
            return field + result!!.deviationDelta
        }
    var rating: Int = rating
        private set
        get() {
            if (result == null) return field
            return field + result!!.ratingDelta
        }

    fun addResult(oldDeviation: Int, newDeviation: Int, oldRating: Int, newRating: Int, score: Int) {
        deviation = oldDeviation
        rating = oldRating
        result = Result(score, newDeviation - oldDeviation, newRating - oldRating)
    }
}
