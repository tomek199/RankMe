package com.tm.rankme.e2e.query

import com.expediagroup.graphql.client.types.GraphQLClientRequest
import kotlin.reflect.KClass

class Info : GraphQLClientRequest<Info.Result> {
    override val query: String = "{info}"

    override fun responseType(): KClass<Result> = Result::class

    data class Result(val info: String)
}