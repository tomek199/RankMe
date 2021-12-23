package com.tm.rankme.e2e.query

import com.expediagroup.graphql.client.types.GraphQLClientRequest
import kotlin.reflect.KClass

class GetPlayer(
    id: String, games: String = ""
) : GraphQLClientRequest<GetPlayer.Result> {

    override val query: String =
        """{
            player(query: {
                id: "$id" 
            }) {
                id name deviation rating
                $games
            }
        }"""

    constructor(id: String, firstGames: Int) : this(
        id,
        """games(first: $firstGames) {
            pageInfo {
                hasPreviousPage hasNextPage startCursor endCursor
            }
            edges {
                cursor
                node {
                    id
                    dateTime
                    playerOneId playerOneName playerOneRating playerOneDeviation
                    playerTwoId playerTwoName playerTwoRating playerTwoDeviation
                    result {
                        playerOneScore playerOneDeviationDelta playerOneRatingDelta
                        playerTwoScore playerTwoDeviationDelta playerTwoRatingDelta
                    }
                }
            }
        }"""
    )

    override fun responseType(): KClass<Result> = Result::class

    data class Result(val player: Player)
}