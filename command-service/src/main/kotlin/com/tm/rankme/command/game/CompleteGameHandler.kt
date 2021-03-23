package com.tm.rankme.command.game

import com.tm.rankme.command.CommandHandler
import com.tm.rankme.domain.base.AggregateException
import com.tm.rankme.domain.base.Event
import com.tm.rankme.domain.base.EventBus
import com.tm.rankme.domain.game.Game
import com.tm.rankme.domain.game.GameRepository
import com.tm.rankme.domain.game.PlayerPort
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Service
import java.util.function.Consumer

@Service
class CompleteGameHandler(
    private val repository: GameRepository,
    private val playerPort: PlayerPort,
    eventBus: EventBus
) : CommandHandler<CompleteGameCommand>(eventBus) {

    @Bean("completeGameCommandHandler")
    override fun dispatch(): Consumer<CompleteGameCommand> = super.dispatch()

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