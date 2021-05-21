package com.tm.rankme.e2e.query

import com.expediagroup.graphql.client.types.GraphQLClientRequest
import java.util.*
import kotlin.reflect.KClass

class GetPlayer(
    id: UUID,
) : GraphQLClientRequest<GetPlayer.Result> {

    override val query: String =
        """{
            getPlayer(query: {
                id: "$id" 
            }) {
                id name deviation rating
            }
        }"""

    override fun responseType(): KClass<Result> = Result::class

    data class Result(
        val getPlayer: Player,
    )
}