package com.tm.rankme.e2e.mutation

import com.expediagroup.graphql.client.types.GraphQLClientRequest
import kotlin.reflect.KClass

class CreateLeague(name: String) : GraphQLClientRequest<CreateLeague.Result> {
    override val query: String =
        """mutation {
            createLeague(command: {name: "$name"})
        }"""

    override fun responseType(): KClass<Result> = Result::class

    data class Result(val createLeague: String)
}