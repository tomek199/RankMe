package com.tm.rankme.e2e.query

import com.expediagroup.graphql.client.types.GraphQLClientRequest
import java.util.*
import kotlin.reflect.KClass

class GetPlayer(
    id: UUID, games: String = ""
) : GraphQLClientRequest<GetPlayer.Result> {

    override val query: String =
        """{
            getPlayer(query: {
                id: "$id" 
            }) {
                id name deviation rating
                $games
            }
        }"""

    constructor(id: UUID, firstGames: Int) : this(
        id,
        """games(first: $firstGames) {
            pageInfo {
                hasPreviousPage
                hasNextPage
                startCursor
                endCursor
            }
            edges {
                cursor
                node {
                    id
                    dateTime
                    playerOneId
                    playerOneName
                    playerOneRating
                    playerOneDeviation
                    playerTwoId
                    playerTwoName
                    playerTwoRating
                    playerTwoDeviation
                    result {
                        playerOneScore
                        playerOneDeviationDelta
                        playerOneRatingDelta
                        playerTwoScore
                        playerTwoDeviationDelta
                        playerTwoRatingDelta
                    }
                }
            }
        }"""
    )

    override fun responseType(): KClass<Result> = Result::class

    data class Result(
        val getPlayer: Player,
    )
}