package com.tm.rankme.command.game

import com.tm.rankme.command.CommandHandler
import com.tm.rankme.domain.base.Event
import com.tm.rankme.domain.base.EventBus
import com.tm.rankme.domain.game.Game
import com.tm.rankme.domain.game.GameRepository
import com.tm.rankme.domain.game.PlayerPort
import org.springframework.amqp.rabbit.annotation.Exchange
import org.springframework.amqp.rabbit.annotation.Queue
import org.springframework.amqp.rabbit.annotation.QueueBinding
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Service

@Service
class ScheduleGameHandler(
    private val repository: GameRepository,
    private val playerPort: PlayerPort,
    eventBus: EventBus
) : com.tm.rankme.command.CommandHandler<ScheduleGameCommand>(eventBus) {

    @RabbitListener(bindings = [
        QueueBinding(
            value = Queue(name = "schedule-game-command-queue"),
            exchange = Exchange(name = "rankme.api", type = "direct"),
            key = ["ScheduleGameCommand"]
        )
    ])
    override fun dispatch(command: ScheduleGameCommand) = super.dispatch(command)

    override fun execute(command: ScheduleGameCommand): List<Event<Game>> {
        val leagueId = playerPort.extractLeagueId(command.playerOneId, command.playerTwoId)
        val game = Game.scheduled(leagueId, command.dateTime, command.playerOneId, command.playerTwoId)
        repository.store(game)
        return game.pendingEvents
    }
}