package com.tm.rankme.command.player

import com.tm.rankme.command.CommandHandler
import com.tm.rankme.domain.base.AggregateException
import com.tm.rankme.domain.base.Event
import com.tm.rankme.domain.base.EventBus
import com.tm.rankme.domain.player.LeaguePort
import com.tm.rankme.domain.player.Player
import com.tm.rankme.domain.player.PlayerRepository
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Service
import java.util.function.Consumer

@Service
class CreatePlayerHandler(
    private val repository: PlayerRepository,
    private val leaguePort: LeaguePort,
    eventBus: EventBus
) : CommandHandler<CreatePlayerCommand>(eventBus) {

    @Bean("createPlayerCommandHandler")
    override fun dispatch(): Consumer<CreatePlayerCommand> = super.dispatch()

    override fun execute(command: CreatePlayerCommand): List<Event<Player>> {
        if (!leaguePort.exist(command.leagueId))
            throw AggregateException("Cannot create player. League ${command.leagueId} does not exist")
        val player = Player.create(command.leagueId, command.name)
        repository.store(player)
        return player.pendingEvents
    }
}