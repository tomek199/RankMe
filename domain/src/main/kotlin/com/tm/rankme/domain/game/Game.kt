package com.tm.rankme.domain.game

import java.time.LocalDateTime

class Game internal constructor(
    val playerOne: Player, val playerTwo: Player,
    val leagueId: String, var dateTime: LocalDateTime, type: Type = Type.SCHEDULED
) {
    var id: String? = null
        private set
    var type: Type = type
        private set

    constructor(
        id: String, playerOne: Player, playerTwo: Player,
        leagueId: String, dateTime: LocalDateTime, type: Type
    ) : this(playerOne, playerTwo, leagueId, dateTime, type) {
        this.id = id
    }

    fun complete(
        deviationOne: Int, ratingOne: Int, scoreOne: Int,
        deviationTwo: Int, ratingTwo: Int, scoreTwo: Int
    ) {
        if (type == Type.COMPLETED) throw IllegalStateException("Game is already completed!")
        type = Type.COMPLETED
        val glicko = GlickoService(deviationOne, ratingOne, scoreOne, deviationTwo, ratingTwo, scoreTwo)
        playerOne.addResult(deviationOne, glicko.playerOneDeviation, ratingOne, glicko.playerOneRating, scoreOne)
        playerTwo.addResult(deviationTwo, glicko.playerTwoDeviation, ratingTwo, glicko.playerTwoRating, scoreTwo)
    }
}
