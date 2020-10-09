package com.tm.rankme.domain.game

class Player(
    val competitorId: String, val username: String,
    deviation: Int, rating: Int, val result: Result? = null
) {
    var deviation: Int = deviation
        private set
        get() {
            if (result == null) return field
            return field + result.deviationDelta
        }
    var rating: Int = rating
        private set
        get() {
            if (result == null) return field
            return field + result.ratingDelta
        }
}
