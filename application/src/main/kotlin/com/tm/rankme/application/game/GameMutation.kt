package com.tm.rankme.application.game

import com.tm.rankme.application.common.logger
import graphql.kickstart.tools.GraphQLMutationResolver
import org.springframework.stereotype.Service

@Service
class GameMutation(private val gameService: GameService) : GraphQLMutationResolver {
    private val log = logger<GameMutation>()

    fun addGame(input: AddGameInput): GameModel {
        log.info("Add game: $input")
        return gameService.create(
            leagueId = input.leagueId,
            firstCompetitorId = input.playerOneId,
            firstScore = input.playerOneScore,
            secondCompetitorId = input.playerTwoId,
            secondScore = input.playerTwoScore
        )
    }
}
