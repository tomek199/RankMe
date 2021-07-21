package com.tm.rankme.domain.player

class GameResult(
    player: Player,
    playerEvent: PlayerPlayedGame,
    opponent: Player,
    opponentEvent: PlayerPlayedGame
) {
    val firstScore: Int = playerEvent.score
    val firstDeviation: Int = player.deviation
    val firstDeviationDelta: Int = playerEvent.deviationDelta
    val firstRating: Int = player.rating
    val firstRatingDelta: Int = playerEvent.ratingDelta
    val secondScore: Int = opponentEvent.score
    val secondDeviation: Int = opponent.deviation
    val secondDeviationDelta: Int = opponentEvent.deviationDelta
    val secondRating: Int = opponent.rating
    val secondRatingDelta: Int = opponentEvent.ratingDelta
}
