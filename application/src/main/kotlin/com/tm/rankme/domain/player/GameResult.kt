package com.tm.rankme.domain.player

class GameResult(playerEvent: PlayerPlayedGame, opponentEvent: PlayerPlayedGame) {
    val firstScore: Int = playerEvent.score
    val firstDeviationDelta: Int = playerEvent.deviationDelta
    val firstRatingDelta: Int = playerEvent.ratingDelta
    val secondScore: Int = opponentEvent.score
    val secondDeviationDelta: Int = opponentEvent.deviationDelta
    val secondRatingDelta: Int = opponentEvent.ratingDelta
}
