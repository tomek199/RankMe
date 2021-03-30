package com.tm.rankme.command.game

import com.tm.rankme.command.CommandHandler
import com.tm.rankme.domain.base.Event
import com.tm.rankme.domain.base.EventBus
import com.tm.rankme.domain.game.Game
import com.tm.rankme.domain.game.GameRepository
import com.tm.rankme.domain.game.PlayerPort
import org.springframework.stereotype.Service
import java.util.function.Consumer

@Service("playGameCommandHandler")
class PlayGameHandler(
    private val repository: GameRepository,
    private val playerPort: PlayerPort,
    eventBus: EventBus
) : CommandHandler<PlayGameCommand>(eventBus), Consumer<PlayGameCommand> {

    override fun accept(command: PlayGameCommand) = dispatch(command)

    override fun execute(command: PlayGameCommand): List<Event<Game>> {
        val game = playerPort.playGame(
            command.playerOneId, command.playerTwoId,
            command.playerOneScore, command.playerTwoScore
        )
        repository.store(game)
        return game.pendingEvents
    }
}