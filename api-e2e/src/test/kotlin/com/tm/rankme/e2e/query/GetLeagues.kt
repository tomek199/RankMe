package com.tm.rankme.e2e.query

import com.expediagroup.graphql.client.types.GraphQLClientRequest
import kotlin.reflect.KClass

class GetLeagues(
    first: Int,
    cursor: RequestCursor? = null
) : GraphQLClientRequest<GetLeagues.Result> {

    override val query: String =
        """{
            leagues(query: {
                first: $first 
                ${if (cursor != null) ", ${cursor.direction}: \"${cursor.value}\"" else ""}
            }) {
                pageInfo {
                    hasPreviousPage hasNextPage startCursor endCursor
                }
                edges {
                    cursor
                    node {
                        id name allowDraws maxScore
                    }
                }
            }
        }"""

    override fun responseType(): KClass<Result> = Result::class

    data class Result(val leagues: Connection<League>)
}