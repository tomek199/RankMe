package com.tm.rankme.e2e.query

import com.expediagroup.graphql.client.types.GraphQLClientRequest
import kotlin.reflect.KClass

class Version : GraphQLClientRequest<Version.Result> {
    override val query: String = "{version}"

    override fun responseType(): KClass<Result> = Result::class

    data class Result(val version: String)
}