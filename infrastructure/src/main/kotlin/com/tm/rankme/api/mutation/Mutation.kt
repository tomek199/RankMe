package com.tm.rankme.api.mutation

import com.tm.rankme.cqrs.command.game.PlayGameCommand
import com.tm.rankme.cqrs.command.game.ScheduleGameCommand
import com.tm.rankme.cqrs.command.league.ChangeLeagueSettingsCommand
import com.tm.rankme.cqrs.command.league.CreateLeagueCommand
import com.tm.rankme.cqrs.command.league.RenameLeagueCommand
import com.tm.rankme.cqrs.command.player.CreatePlayerCommand
import graphql.kickstart.tools.GraphQLMutationResolver
import org.springframework.stereotype.Service

@Service
class Mutation(
    private val executor: CommandExecutor
) : GraphQLMutationResolver {

    fun createLeague(command: CreateLeagueCommand) = executor.execute(command)

    fun renameLeague(command: RenameLeagueCommand) = executor.execute(command)

    fun changeLeagueSettings(command: ChangeLeagueSettingsCommand) = executor.execute(command)

    fun createPlayer(command: CreatePlayerCommand) = executor.execute(command)

    fun playGame(command: PlayGameCommand) = executor.execute(command)

    fun scheduleGame(command: ScheduleGameCommand) = executor.execute(command)
}