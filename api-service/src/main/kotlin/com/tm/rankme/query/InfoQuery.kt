package com.tm.rankme.query

import graphql.kickstart.tools.GraphQLQueryResolver
import org.springframework.boot.info.BuildProperties
import org.springframework.stereotype.Service

@Service
class InfoQuery(
    private val buildProperties: BuildProperties,
) : GraphQLQueryResolver {

    fun version(): String = buildProperties.version
}