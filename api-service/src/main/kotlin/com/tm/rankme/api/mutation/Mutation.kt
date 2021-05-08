package com.tm.rankme.api.mutation

import graphql.kickstart.tools.GraphQLMutationResolver
import org.springframework.stereotype.Service

@Service
class Mutation(
    private val bus: CommandBus
) : GraphQLMutationResolver {

    private final val result = "SUBMITTED"

    fun createLeague(command: CreateLeagueCommand) = bus.execute(command).run { result }
    fun renameLeague(command: RenameLeagueCommand) = bus.execute(command).run { result }
    fun changeLeagueSettings(command: ChangeLeagueSettingsCommand) = bus.execute(command).run { result }
    fun createPlayer(command: CreatePlayerCommand) = bus.execute(command).run { result }
    fun playGame(command: PlayGameCommand) = bus.execute(command).run { result }
    fun scheduleGame(command: ScheduleGameCommand) = bus.execute(command).run { result }
    fun completeGame(command: CompleteGameCommand) = bus.execute(command).run { result }
}