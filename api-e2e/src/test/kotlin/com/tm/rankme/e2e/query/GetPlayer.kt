package com.tm.rankme.e2e.query

import com.expediagroup.graphql.client.types.GraphQLClientRequest
import kotlin.reflect.KClass

class GetPlayer private constructor(
    id: String,
    games: String = "", completedGames: String = "", scheduledGames: String = ""
) : GraphQLClientRequest<GetPlayer.Result> {

    override val query: String =
        """{
            player(query: {
                id: "$id" 
            }) {
                id name deviation rating
                $games
                $completedGames
                $scheduledGames
            }
        }"""

    constructor(id: String) : this(id, "", "", "")

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
                    ... on CompletedGame {
                        result {
                            playerOneScore playerOneDeviationDelta playerOneRatingDelta
                            playerTwoScore playerTwoDeviationDelta playerTwoRatingDelta
                        }
                    }
                }
            }
        }"""
    )

    constructor(id: String, numberOfCompletedGames: Int, numberOfScheduledGames: Int) : this(
        id,
        "",
        """completedGames(first: $numberOfCompletedGames) {
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
        }""",
        """scheduledGames(first: $numberOfScheduledGames) {
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
                }
            }
        }"""
    )

    override fun responseType(): KClass<Result> = Result::class

    data class Result(val player: Player)
}