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
            }
        }"""

    override fun responseType(): KClass<Result> = Result::class

    data class Result(
        val getLeague: League,
    )

    data class League(
        val id: UUID,
        val name: String,
        val allowDraws: Boolean,
        val maxScore: Int
    )
}