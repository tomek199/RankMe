package com.tm.rankme.cqrs.command.player

import com.tm.rankme.cqrs.command.CommandHandler
import com.tm.rankme.domain.base.AggregateException
import com.tm.rankme.domain.player.LeaguePort
import com.tm.rankme.domain.player.Player
import com.tm.rankme.domain.player.PlayerRepository
import org.springframework.stereotype.Service

@Service
class CreatePlayerHandler(
    private val repository: PlayerRepository,
    private val leaguePort: LeaguePort
) : CommandHandler<CreatePlayerCommand> {

    override fun dispatch(command: CreatePlayerCommand) {
        if (!leaguePort.exist(command.leagueId))
            throw AggregateException("Cannot create player. League ${command.leagueId} does not exist")
        val player = Player.create(command.leagueId, command.name)
        repository.store(player)
    }
}