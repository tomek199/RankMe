package com.tm.rankme.e2e.query

import com.expediagroup.graphql.client.types.GraphQLClientRequest
import kotlin.reflect.KClass

class GetCompletedGames(
    leagueId: String,
    first: Int,
    after: String? = null
) : GraphQLClientRequest<GetCompletedGames.Result> {

    override val query: String =
        """{
            completedGames(query: {
                leagueId: "$leagueId", 
                first: $first 
                ${if (after != null) ", after: \"$after\"" else ""}
            }) {
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
            }
        }"""

    override fun responseType(): KClass<Result> = Result::class

    data class Result(val completedGames: Connection<CompletedGame>)
}