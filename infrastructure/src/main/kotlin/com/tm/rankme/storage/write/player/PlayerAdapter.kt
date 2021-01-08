package com.tm.rankme.storage.write.player

import com.tm.rankme.domain.base.AggregateException
import com.tm.rankme.domain.game.Game
import com.tm.rankme.domain.game.PlayerPort
import com.tm.rankme.domain.game.Result
import com.tm.rankme.domain.player.Player
import com.tm.rankme.domain.player.PlayerRepository
import java.util.*
import org.springframework.stereotype.Service

@Service
class PlayerAdapter(private val repository: PlayerRepository) : PlayerPort {
    override fun playGame(firstPlayerId: UUID, secondPlayerId: UUID, firstScore: Int, secondScore: Int): Game {
        val firstPlayer = repository.byId(firstPlayerId)
        val secondPlayer = repository.byId(secondPlayerId)
        val leagueId = extractLeagueId(firstPlayer, secondPlayer)
        val result = firstPlayer.playedWith(secondPlayer, firstScore, secondScore)
        val firstPlayerResult = Result(result.firstScore, result.firstDeviationDelta, result.firstRatingDelta)
        val secondPlayerResult = Result(result.secondScore, result.secondDeviationDelta, result.secondRatingDelta)
        return Game.played(leagueId, firstPlayerId, secondPlayerId, firstPlayerResult, secondPlayerResult)
    }

    private fun extractLeagueId(firstPlayer: Player, secondPlayer: Player): UUID  {
        if (firstPlayer.leagueId != secondPlayer.leagueId)
            throw AggregateException("Players ${firstPlayer.id} and ${secondPlayer.id} does not belong to the same league")
        return firstPlayer.leagueId
    }
}