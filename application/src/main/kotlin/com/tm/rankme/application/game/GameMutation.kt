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
        val game = GameFactory.completedMatch(Pair(firstCompetitor, playerOneScore),
                Pair(secondCompetitor, playerTwoScore), leagueId)
        firstCompetitor.updateStatistics(game.playerOne, playerTwoScore, game.dateTime)
        secondCompetitor.updateStatistics(game.playerTwo, playerTwoScore, game.dateTime)
        competitorRepository.save(firstCompetitor)
        competitorRepository.save(secondCompetitor)
        return mapper.toModel(gameRepository.save(game))
    }

    fun addScheduledGame(leagueId: String, playerOneId: String, playerTwoId: String,
                         dateTime: LocalDateTime): GameModel {
        val firstCompetitor = getCompetitor(playerOneId, leagueId)
        val secondCompetitor = getCompetitor(playerTwoId, leagueId)
        val game = GameFactory.scheduledMatch(firstCompetitor, secondCompetitor, leagueId, dateTime)
        return mapper.toModel(gameRepository.save(game))
    }

    fun getCompetitor(id: String, leagueId: String): Competitor {
        val competitor = competitorRepository.findById(id) ?: throw IllegalStateException("Competitor $id not fount")
        if (competitor.leagueId != leagueId)
            throw IllegalStateException("Competitor is not assigned to league $leagueId")
        return competitor
    }
}