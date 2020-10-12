package com.tm.rankme.domain.game

import java.time.LocalDateTime

class Game internal constructor(
    val playerOne: Player, val playerTwo: Player,
    val leagueId: String, var dateTime: LocalDateTime
) {
    var id: String? = null
        private set

    constructor(id: String, playerOne: Player, playerTwo: Player, leagueId: String, dateTime: LocalDateTime)
        : this(playerOne, playerTwo, leagueId, dateTime) {
        this.id = id
    }

    fun complete(
        deviationOne: Int, ratingOne: Int, scoreOne: Int,
        deviationTwo: Int, ratingTwo: Int, scoreTwo: Int) {
        val glicko = GlickoService(deviationOne, ratingOne, scoreOne, deviationTwo, ratingTwo, scoreTwo)
        playerOne.addResult(deviationOne, glicko.playerOneDeviation, ratingOne, glicko.playerOneRating, scoreOne)
        playerTwo.addResult(deviationTwo, glicko.playerTwoDeviation, ratingTwo, glicko.playerTwoRating, scoreTwo)
    }
}
