package com.tm.rankme.cqrs.command.game

import com.tm.rankme.cqrs.command.CommandHandler
import com.tm.rankme.domain.base.AggregateException
import com.tm.rankme.domain.base.Event
import com.tm.rankme.domain.base.EventBus
import com.tm.rankme.domain.game.Game
import com.tm.rankme.domain.game.GameRepository
import com.tm.rankme.domain.game.PlayerPort
import org.springframework.stereotype.Service

@Service
class CompleteGameHandler(
    private val repository: GameRepository,
    private val playerPort: PlayerPort,
    eventBus: EventBus
) : CommandHandler<CompleteGameCommand>(eventBus) {

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