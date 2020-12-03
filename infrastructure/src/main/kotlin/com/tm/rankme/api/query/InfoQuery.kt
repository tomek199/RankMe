package com.tm.rankme.api.query

import graphql.kickstart.tools.GraphQLQueryResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.info.BuildProperties
import org.springframework.stereotype.Service

@Service
class InfoQuery @Autowired constructor(
    private val buildProperties: BuildProperties
) : GraphQLQueryResolver {

    fun info(): String = "RankMe GraphQL API ${buildProperties.version}"
}