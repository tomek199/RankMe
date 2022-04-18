package com.tm.rankme.e2e.query

import com.expediagroup.graphql.client.types.GraphQLClientRequest
import kotlin.reflect.KClass

class GetPlayerScheduledGames(
    playerId: String,
    first: Int,
    cursor: RequestCursor? = null
) : GraphQLClientRequest<GetPlayerScheduledGames.Result> {

    override val query: String =
        """{
            playerScheduledGames(query: {
                playerId: "$playerId", 
                first: $first 
                ${if (cursor != null) ", ${cursor.direction}: \"${cursor.value}\"" else ""}
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
                    }
                }
            }
        }"""

    override fun responseType(): KClass<Result> = Result::class

    data class Result(val playerScheduledGames: Connection<ScheduledGame>)
}