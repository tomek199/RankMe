package com.tm.rankme.e2e.mutation

import com.expediagroup.graphql.client.types.GraphQLClientRequest
import kotlin.reflect.KClass

class CompleteGame(
    gameId: String,
    playerOneScore: Int,
    playerTwoScore: Int
) : GraphQLClientRequest<CompleteGame.Result> {

    override val query: String =
        """mutation {
            completeGame(command: {
                gameId: "$gameId", 
                playerOneScore: $playerOneScore, 
                playerTwoScore: $playerTwoScore, 
            })
        }"""

    override fun responseType(): KClass<Result> = Result::class

    data class Result(val completeGame: String)
}