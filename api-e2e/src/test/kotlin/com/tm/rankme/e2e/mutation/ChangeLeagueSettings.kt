package com.tm.rankme.e2e.mutation

import com.expediagroup.graphql.client.types.GraphQLClientRequest
import java.util.*
import kotlin.reflect.KClass

class ChangeLeagueSettings(
    id: UUID,
    allowDraws: Boolean,
    maxScore: Int
) : GraphQLClientRequest<ChangeLeagueSettings.Result> {

    override val query: String =
        """mutation {
            changeLeagueSettings(command: {
                id: "$id", 
                allowDraws: $allowDraws, 
                maxScore: $maxScore
            })
        }"""

    override fun responseType(): KClass<Result> = Result::class

    data class Result(val changeLeagueSettings: String)
}