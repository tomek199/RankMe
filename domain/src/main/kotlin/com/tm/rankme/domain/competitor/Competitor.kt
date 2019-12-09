package com.tm.rankme.domain.competitor

import com.tm.rankme.domain.game.Player
import java.time.LocalDateTime

class Competitor(val leagueId: String, var username: String,
                 val statistics: Statistics = Statistics(), var status: Status = Status.ACTIVE) {
    var id: String? = null
        private set

    constructor(leagueId: String, id: String, username: String, statistics: Statistics)
            : this(leagueId, username, statistics) {
        this.id = id
    }

    fun updateStatistics(player: Player, opponentScore: Int, gameDateTime: LocalDateTime) {
        val score = player.score?: throw IllegalArgumentException("Player does not contain score value!")
        statistics.addGame(score, opponentScore)
        statistics.lastGame = gameDateTime.toLocalDate()
        statistics.deviation = player.deviation
        statistics.rating = player.rating
    }
}
