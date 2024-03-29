package com.tm.rankme.e2e.mutation

import com.expediagroup.graphql.client.types.GraphQLClientRequest
import kotlin.reflect.KClass

class RenameLeague(
    id: String,
    name: String
) : GraphQLClientRequest<RenameLeague.Result> {

    override val query: String =
        """mutation {
            renameLeague(command: {
                id: "$id", 
                name: "$name", 
            })
        }"""

    override fun responseType(): KClass<Result> = Result::class

    data class Result(val renameLeague: String)
}