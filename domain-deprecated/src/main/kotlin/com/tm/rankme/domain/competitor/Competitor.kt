package com.tm.rankme.domain.competitor

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sqrt

class Competitor(val leagueId: String, var username: String) {
    var id: String? = null
        private set
    var deviation: Int = 350
        get() {
            if (lastGame == null)
                return field
            val c = 48
            val weeks = ChronoUnit.WEEKS.between(lastGame, LocalDate.now()) + 1
            val newDeviation = sqrt(((field * field) + (c * c) * weeks).toDouble()).roundToInt()
            return min(350, newDeviation)
        }
        internal set
    var rating: Int = 1500
        internal set
    var lastGame: LocalDate? = null
        internal set

    constructor(
        leagueId: String, id: String, username: String,
        deviation: Int = 350, rating: Int = 1500, lastGame: LocalDate? = null
    ) : this(leagueId, username) {
        this.id = id
        this.deviation = deviation
        this.rating = rating
        this.lastGame = lastGame
    }

    fun updateAfterGame(deviation: Int, rating: Int, gameDateTime: LocalDateTime) {
        this.lastGame = gameDateTime.toLocalDate()
        this.deviation = deviation
        this.rating = rating
    }
}
