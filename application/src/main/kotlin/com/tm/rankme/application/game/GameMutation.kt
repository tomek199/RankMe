package com.tm.rankme.application.game

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import com.tm.rankme.application.common.Mapper
import com.tm.rankme.application.competitor.CompetitorService
import com.tm.rankme.domain.game.Game
import com.tm.rankme.domain.game.GameFactory
import com.tm.rankme.domain.game.GameRepository
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
class GameMutation(
    private val gameRepository: GameRepository,
    private val competitorService: CompetitorService,
    @Qualifier("gameMapper") private val mapper: Mapper<Game, GameModel>
) : GraphQLMutationResolver {

    fun addGame(
        leagueId: String, playerOneId: String, playerOneScore: Int,
        playerTwoId: String, playerTwoScore: Int
    ): GameModel {
        val firstCompetitor = competitorService.getCompetitor(playerOneId, leagueId)
        val secondCompetitor = competitorService.getCompetitor(playerTwoId, leagueId)
        val game = GameFactory.create(
            firstCompetitor, playerOneScore,
            secondCompetitor, playerTwoScore, leagueId
        )
        competitorService.updateCompetitorsStatistic(firstCompetitor, secondCompetitor, game)
        return mapper.toModel(gameRepository.save(game))
    }
}