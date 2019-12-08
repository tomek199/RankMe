package com.tm.rankme.domain.game

import com.tm.rankme.domain.competitor.Competitor
import java.time.LocalDate

class Game(first: Competitor, second: Competitor, var date: LocalDate) {
    val id: String? = null
    val playerOne: Player = first.id?.let {
            Player(it, first.username, first.statistics.deviation, first.statistics.rating)
        } ?: throw IllegalStateException("Competitor ids cannot be null!")
    val playerTwo: Player = second.id?.let {
            Player(it, second.username, second.statistics.deviation, second.statistics.rating)
        } ?: throw IllegalStateException("Competitor ids cannot be null!")
}
