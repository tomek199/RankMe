package com.tm.rankme.domain.player

import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sqrt

class LeagueStats(val leagueId: String) {
    var rating: Int = 1500
        internal set
    var deviation: Int = 350
        get() {
            if (lastMatch == null)
                return field
            val c = 48
            val weeks = ChronoUnit.WEEKS.between(lastMatch, LocalDate.now()) + 1
            val newDeviation = sqrt(((field * field) + (c * c) * weeks).toDouble()).roundToInt()
            return min(350, newDeviation)
        }
        internal set
    var won: Int = 0
        internal set
    var lost: Int = 0
        internal set
    var draw: Int = 0
        internal set
    var lastMatch: LocalDate? = null
        internal set
}