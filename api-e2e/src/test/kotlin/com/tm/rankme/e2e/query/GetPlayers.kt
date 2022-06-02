package com.tm.rankme.e2e.query

import com.expediagroup.graphql.client.types.GraphQLClientRequest
import kotlin.reflect.KClass

class GetPlayers(
    leagueId: String,
) : GraphQLClientRequest<GetPlayers.Result> {

    override val query: String =
        """{
            players(query: {
                leagueId: "$leagueId" 
            }) {
                id name deviation rating
            }
        }"""

    override fun responseType(): KClass<Result> = Result::class

    data class Result(val players: List<Player>)
}