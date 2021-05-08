package com.tm.rankme.e2e.mutation

import com.expediagroup.graphql.client.types.GraphQLClientRequest
import java.util.*
import kotlin.reflect.KClass

class CreatePlayer(leagueId: UUID, name: String) : GraphQLClientRequest<CreatePlayer.Result> {
    override val query: String =
        """mutation {
            createPlayer(command: {leagueId: "$leagueId", name: "$name"})
        }"""

    override fun responseType(): KClass<Result> = Result::class

    data class Result(val createPlayer: String)
}