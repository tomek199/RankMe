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

@Service
class GameMutation(
    private val gameRepository: GameRepository,
    private val competitorRepository: CompetitorRepository,
    @Qualifier("gameMapper") private val mapper: Mapper<Game, GameModel>
) : GraphQLMutationResolver {

    fun addGame(
        leagueId: String, playerOneId: String, playerOneScore: Int,
        playerTwoId: String, playerTwoScore: Int
    ): GameModel {
        val firstCompetitor = getCompetitor(playerOneId, leagueId)
        val secondCompetitor = getCompetitor(playerTwoId, leagueId)
        val game = GameFactory.create(
            firstCompetitor, playerOneScore,
            secondCompetitor, playerTwoScore, leagueId
        )
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
        firstCompetitor.updateStatistics(game.playerOne, game.playerTwo.score, game.dateTime)
        competitorRepository.save(firstCompetitor)
        secondCompetitor.updateStatistics(game.playerTwo, game.playerOne.score, game.dateTime)
        competitorRepository.save(secondCompetitor)
    }
}