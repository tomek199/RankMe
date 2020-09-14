package com.tm.rankme.application.game

import graphql.kickstart.tools.GraphQLMutationResolver
import org.springframework.stereotype.Service

@Service
class GameMutation(
    private val gameService: GameService,
) : GraphQLMutationResolver {

    fun addGame(input: AddGameInput): GameModel {
        return gameService.create(
            leagueId = input.leagueId,
            firstCompetitorId = input.playerOneId,
            firstScore = input.playerOneScore,
            secondCompetitorId = input.playerTwoId,
            secondScore = input.playerTwoScore
        )
    }

    fun completeGame(input: CompleteGameInput): GameModel {
        return gameService.complete(input.eventId, input.playerOneScore, input.playerTwoScore)
    }
}
