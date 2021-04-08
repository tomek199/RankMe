package com.tm.rankme.e2e

import com.expediagroup.graphql.client.types.GraphQLClientRequest
import kotlin.reflect.KClass

class InfoQuery : GraphQLClientRequest<InfoQuery.Result> {
    override val query: String = "{info}"

    override fun responseType(): KClass<Result> = Result::class

    data class Result(val info: String)
}