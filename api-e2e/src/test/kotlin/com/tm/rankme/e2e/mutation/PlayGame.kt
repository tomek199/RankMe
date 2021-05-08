package com.tm.rankme.e2e.mutation

import com.expediagroup.graphql.client.types.GraphQLClientRequest
import java.util.*
import kotlin.reflect.KClass

class PlayGame(
    playerOneId: UUID,
    playerTwoId: UUID,
    playerOneScore: Int,
    playerTwoScore: Int
) : GraphQLClientRequest<PlayGame.Result> {

    override val query: String =
        """mutation {
            playGame(command: {
                playerOneId: "$playerOneId", 
                playerTwoId: "$playerTwoId", 
                playerOneScore: $playerOneScore, 
                playerTwoScore: $playerTwoScore, 
            })
        }"""

    override fun responseType(): KClass<Result> = Result::class

    data class Result(val playGame: String)
}