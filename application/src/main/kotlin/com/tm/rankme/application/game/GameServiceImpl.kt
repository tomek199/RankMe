package com.tm.rankme.application.game

import com.tm.rankme.domain.Side
import com.tm.rankme.domain.competitor.Competitor
import com.tm.rankme.domain.game.Game
import com.tm.rankme.domain.game.GameFactory
import com.tm.rankme.domain.game.GameRepository
import org.springframework.stereotype.Service

@Service
internal class GameServiceImpl(
    private val repository: GameRepository
) : GameService {

    override fun get(gameId: String): Game {
        val game = repository.findById(gameId)
        return game ?: throw IllegalStateException("Game $gameId is not found")
    }

    override fun create(
        leagueId: String,
        firstCompetitor: Competitor, firstScore: Int,
        secondCompetitor: Competitor, secondScore: Int
    ): Game {
        val game = GameFactory.create(firstCompetitor, firstScore, secondCompetitor, secondScore, leagueId)
        return repository.save(game)
    }

    override fun getSideForLeague(leagueId: String, first: Int, after: String?): Side<Game> {
        return repository.findByLeagueId(leagueId, first, after)
    }
}