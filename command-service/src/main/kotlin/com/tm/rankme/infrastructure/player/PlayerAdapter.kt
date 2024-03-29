package com.tm.rankme.infrastructure.player

import com.tm.rankme.domain.base.AggregateException
import com.tm.rankme.domain.base.EventBus
import com.tm.rankme.domain.game.Game
import com.tm.rankme.domain.game.PlayerPort
import com.tm.rankme.domain.game.Result
import com.tm.rankme.domain.player.Player
import com.tm.rankme.domain.player.PlayerRepository
import org.springframework.stereotype.Service

@Service
class PlayerAdapter(
    private val repository: PlayerRepository,
    private val eventBus: EventBus
) : PlayerPort {
    override fun playGame(firstPlayerId: String, secondPlayerId: String, firstScore: Int, secondScore: Int): Game {
        val firstPlayer = repository.byId(firstPlayerId)
        val secondPlayer = repository.byId(secondPlayerId)
        val leagueId = extractLeagueId(firstPlayer, secondPlayer)
        val result = firstPlayer.playedWith(secondPlayer, firstScore, secondScore)
        storePlayers(firstPlayer, secondPlayer)
        return Game.played(
            leagueId, firstPlayerId, secondPlayerId,
            Result(result.firstScore, result.firstDeviation, result.firstDeviationDelta, result.firstRating, result.firstRatingDelta),
            Result(result.secondScore, result.secondDeviation, result.secondDeviationDelta, result.secondRating, result.secondRatingDelta)
        )
    }

    override fun extractLeagueId(firstPlayerId: String, secondPlayerId: String): String {
        val firstPlayer = repository.byId(firstPlayerId)
        val secondPlayer = repository.byId(secondPlayerId)
        return extractLeagueId(firstPlayer, secondPlayer)
    }

    private fun extractLeagueId(firstPlayer: Player, secondPlayer: Player): String  {
        if (firstPlayer.leagueId != secondPlayer.leagueId)
            throw AggregateException("Players ${firstPlayer.id} and ${secondPlayer.id} does not belong to the same league")
        return firstPlayer.leagueId
    }

    private fun storePlayers(firstPlayer: Player, secondPlayer: Player) {
        repository.store(firstPlayer)
        repository.store(secondPlayer)
        firstPlayer.pendingEvents.forEach(eventBus::emit)
        secondPlayer.pendingEvents.forEach(eventBus::emit)
    }
}