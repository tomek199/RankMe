package com.tm.rankme.application.game

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import com.tm.rankme.application.common.Mapper
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
    fun addCompletedGame(leagueId: String, playerOneId: String, playerOneScore: Int,
                         playerTwoId: String, playerTwoScore: Int): GameModel {
        val firstCompetitor = competitorRepository.findById(playerOneId)
                ?: throw IllegalStateException("Competitor $playerOneId not fount")
        val secondCompetitor = competitorRepository.findById(playerTwoId)
                ?: throw IllegalStateException("Competitor $playerTwoId not fount")
        if (firstCompetitor.leagueId != leagueId || secondCompetitor.leagueId != leagueId)
            throw IllegalStateException("Competitor is not assigned to league $leagueId")

        val game = GameFactory.completedMatch(Pair(firstCompetitor, playerOneScore),
                Pair(secondCompetitor, playerTwoScore), leagueId)
        return mapper.toModel(gameRepository.save(game))
    }
}