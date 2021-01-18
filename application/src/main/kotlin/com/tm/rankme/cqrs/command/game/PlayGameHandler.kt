package com.tm.rankme.cqrs.command.game

import com.tm.rankme.cqrs.command.CommandHandler
import com.tm.rankme.domain.game.GameRepository
import com.tm.rankme.domain.game.PlayerPort
import org.springframework.stereotype.Service

@Service
class PlayGameHandler(
    private val repository: GameRepository,
    private val playerPort: PlayerPort
) : CommandHandler<PlayGameCommand> {

    override fun dispatch(command: PlayGameCommand) {
        val game = playerPort.playGame(
            command.playerOneId, command.playerTwoId,
            command.playerOneScore, command.playerTwoScore
        )
        repository.store(game)
    }
}