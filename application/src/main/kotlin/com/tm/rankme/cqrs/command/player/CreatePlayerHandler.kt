package com.tm.rankme.cqrs.command.player

import com.tm.rankme.cqrs.command.CommandHandler
import com.tm.rankme.domain.player.Player
import com.tm.rankme.domain.player.PlayerRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class CreatePlayerHandler @Autowired constructor(
    private val repository: PlayerRepository
) : CommandHandler<CreatePlayerCommand> {

    override fun dispatch(command: CreatePlayerCommand) {
        val player = Player.create(command.leagueId, command.name)
        repository.store(player)
    }
}