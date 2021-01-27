package com.tm.rankme.cqrs.command.game

import com.tm.rankme.cqrs.command.CommandHandler
import com.tm.rankme.domain.base.Event
import com.tm.rankme.domain.base.EventBus
import com.tm.rankme.domain.game.Game
import com.tm.rankme.domain.game.GameRepository
import com.tm.rankme.domain.game.PlayerPort
import org.springframework.stereotype.Service

@Service
class PlayGameHandler(
    private val repository: GameRepository,
    private val playerPort: PlayerPort,
    eventBus: EventBus
) : CommandHandler<PlayGameCommand>(eventBus) {

    override fun execute(command: PlayGameCommand): List<Event<Game>> {
        val game = playerPort.playGame(
            command.playerOneId, command.playerTwoId,
            command.playerOneScore, command.playerTwoScore
        )
        repository.store(game)
        return game.pendingEvents
    }
}