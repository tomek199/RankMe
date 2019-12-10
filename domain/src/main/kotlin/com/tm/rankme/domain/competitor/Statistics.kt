package com.tm.rankme.domain.competitor

import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sqrt

class Statistics() {
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
    var won: Int = 0
        private set
    var lost: Int = 0
        private set
    var draw: Int = 0
        private set
    var lastGame: LocalDate? = null
        internal set

    constructor(deviation: Int, rating: Int, won: Int, lost: Int, draw: Int, lastGame: LocalDate?): this() {
        this.deviation = deviation
        this.rating = rating
        this.won = won
        this.lost = lost
        this.draw = draw
        this.lastGame = lastGame
    }

    fun addGame(score: Int, opponentScore: Int) {
        when {
            score > opponentScore -> won++
            score < opponentScore -> lost++
            else -> draw++
        }
    }
}