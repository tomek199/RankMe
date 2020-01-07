package com.tm.rankme.application

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import org.springframework.stereotype.Service

/**
 * Temporary test resolver
 */
@Service
class TestResolver : GraphQLQueryResolver {

    fun tests(): List<Test> {
        var test1 = Test("Value1", 4)
        var test2 = Test("Value2", 8)
        return listOf(test1, test2)
    }
}