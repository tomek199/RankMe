package com.tm.rankme.cqrs.command.game

import com.tm.rankme.cqrs.command.CommandHandler
import com.tm.rankme.domain.base.AggregateException
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
class CompleteGameHandler(
    private val repository: GameRepository,
    private val playerPort: PlayerPort,
    eventBus: EventBus
) : CommandHandler<CompleteGameCommand>(eventBus) {

    @RabbitListener(bindings = [
        QueueBinding(
            value = Queue(name = "complete-game-command-queue"),
            exchange = Exchange(name = "rankme.api", type = "direct"),
            key = ["CompleteGameCommand"]
        )
    ])
    override fun dispatch(command: CompleteGameCommand) = super.dispatch(command)

    override fun execute(command: CompleteGameCommand): List<Event<Game>> {
        val game = repository.byId(command.gameId)
        if (!game.canComplete()) throw AggregateException("Game ${game.id} is already played")
        val playedGame = playerPort.playGame(
            game.playerIds.first, game.playerIds.second,
            command.playerOneScore, command.playerTwoScore
        )
        game.complete(playedGame.result!!.first, playedGame.result!!.second)
        repository.store(game)
        return game.pendingEvents
    }
}