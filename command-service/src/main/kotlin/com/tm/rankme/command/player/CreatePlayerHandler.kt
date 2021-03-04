package com.tm.rankme.command.player

import com.tm.rankme.command.CommandHandler
import com.tm.rankme.domain.base.AggregateException
import com.tm.rankme.domain.base.Event
import com.tm.rankme.domain.base.EventBus
import com.tm.rankme.domain.player.LeaguePort
import com.tm.rankme.domain.player.Player
import com.tm.rankme.domain.player.PlayerRepository
import org.springframework.amqp.rabbit.annotation.Exchange
import org.springframework.amqp.rabbit.annotation.Queue
import org.springframework.amqp.rabbit.annotation.QueueBinding
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Service

@Service
class CreatePlayerHandler(
    private val repository: PlayerRepository,
    private val leaguePort: LeaguePort,
    eventBus: EventBus
) : com.tm.rankme.command.CommandHandler<CreatePlayerCommand>(eventBus) {

    @RabbitListener(bindings = [
        QueueBinding(
            value = Queue(name = "create-player-command-queue"),
            exchange = Exchange(name = "rankme.api", type = "direct"),
            key = ["CreatePlayerCommand"]
        )
    ])
    override fun dispatch(command: CreatePlayerCommand) = super.dispatch(command)

    override fun execute(command: CreatePlayerCommand): List<Event<Player>> {
        if (!leaguePort.exist(command.leagueId))
            throw AggregateException("Cannot create player. League ${command.leagueId} does not exist")
        val player = Player.create(command.leagueId, command.name)
        repository.store(player)
        return player.pendingEvents
    }
}