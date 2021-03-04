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
class PlayGameHandler(
    private val repository: GameRepository,
    private val playerPort: PlayerPort,
    eventBus: EventBus
) : com.tm.rankme.command.CommandHandler<PlayGameCommand>(eventBus) {

    @RabbitListener(bindings = [
        QueueBinding(
            value = Queue(name = "play-game-command-queue"),
            exchange = Exchange(name = "rankme.api", type = "direct"),
            key = ["PlayGameCommand"]
        )
    ])
    override fun dispatch(command: PlayGameCommand) = super.dispatch(command)

    override fun execute(command: PlayGameCommand): List<Event<Game>> {
        val game = playerPort.playGame(
            command.playerOneId, command.playerTwoId,
            command.playerOneScore, command.playerTwoScore
        )
        repository.store(game)
        return game.pendingEvents
    }
}