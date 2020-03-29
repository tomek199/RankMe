package com.tm.rankme.application.game

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import com.tm.rankme.application.common.Mapper
import com.tm.rankme.domain.competitor.Competitor
import com.tm.rankme.domain.competitor.CompetitorRepository
import com.tm.rankme.domain.game.Game
import com.tm.rankme.domain.game.GameFactory
import com.tm.rankme.domain.game.GameRepository
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class GameMutation(
        private val gameRepository: GameRepository,
        private val competitorRepository: CompetitorRepository,
        @Qualifier("gameMapper") private val mapper: Mapper<Game, GameModel>
) : GraphQLMutationResolver {
    fun addCompletedGame(leagueId: String, playerOneId: String, playerOneScore: Int,
                         playerTwoId: String, playerTwoScore: Int): GameModel {
        val firstCompetitor = getCompetitor(playerOneId, leagueId)
        val secondCompetitor = getCompetitor(playerTwoId, leagueId)
        val game = GameFactory.completedGame(Pair(firstCompetitor, playerOneScore),
                Pair(secondCompetitor, playerTwoScore), leagueId)
        updateCompetitorStatistics(firstCompetitor, secondCompetitor, game)
        return mapper.toModel(gameRepository.save(game))
    }

    fun addScheduledGame(leagueId: String, playerOneId: String, playerTwoId: String,
                         dateTime: LocalDateTime): GameModel {
        val firstCompetitor = getCompetitor(playerOneId, leagueId)
        val secondCompetitor = getCompetitor(playerTwoId, leagueId)
        val game = GameFactory.scheduledGame(firstCompetitor, secondCompetitor, leagueId, dateTime)
        return mapper.toModel(gameRepository.save(game))
    }

    fun completeGame(id: String, playerOneScore: Int, playerTwoScore: Int): GameModel {
        val game = gameRepository.findById(id) ?: throw java.lang.IllegalStateException("Game $id is not found")
        if (game.playerOne.score != null || game.playerTwo.score != null)
            throw IllegalStateException("Game $id is already completed")
        val firstCompetitor = getCompetitor(game.playerOne.competitorId, game.leagueId)
        val secondCompetitor = getCompetitor(game.playerTwo.competitorId, game.leagueId)
        game.complete(Pair(firstCompetitor, playerOneScore), Pair(secondCompetitor, playerTwoScore))
        updateCompetitorStatistics(firstCompetitor, secondCompetitor, game)
        return mapper.toModel(gameRepository.save(game))
    }

    private fun getCompetitor(id: String, leagueId: String): Competitor {
        val competitor = competitorRepository.findById(id) ?: throw IllegalStateException("Competitor $id is not found")
        if (competitor.leagueId != leagueId)
            throw IllegalStateException("Competitor $id is not assigned to league $leagueId")
        return competitor
    }

    private fun updateCompetitorStatistics(firstCompetitor: Competitor, secondCompetitor: Competitor, game: Game) {
        game.playerTwo.score?.let {
            firstCompetitor.updateStatistics(game.playerOne, it, game.dateTime)
            competitorRepository.save(firstCompetitor)
        }
        game.playerOne.score?.let {
            secondCompetitor.updateStatistics(game.playerTwo, it, game.dateTime)
            competitorRepository.save(secondCompetitor)
        }
    }
}