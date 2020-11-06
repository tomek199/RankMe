package com.tm.rankme.api.mutation

import com.tm.rankme.application.league.CreateLeagueCommand
import graphql.kickstart.tools.GraphQLMutationResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class Mutation @Autowired constructor(
    private val executor: CommandExecutor
) : GraphQLMutationResolver {

    fun createLeague(command: CreateLeagueCommand): Result = executor.execute(command)
}