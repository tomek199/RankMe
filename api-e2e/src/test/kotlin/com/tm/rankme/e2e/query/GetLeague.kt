package com.tm.rankme.e2e.query

import com.expediagroup.graphql.client.types.GraphQLClientRequest
import java.util.*
import kotlin.reflect.KClass

class GetLeague(
    id: UUID,
) : GraphQLClientRequest<GetLeague.Result> {

    override val query: String =
        """{
            getLeague(query: {
                id: "$id" 
            }) {
                id name allowDraws maxScore
                players {
                    id name deviation rating
                }
            }
        }"""

    override fun responseType(): KClass<Result> = Result::class

    data class Result(
        val getLeague: League,
    )
}