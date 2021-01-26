package com.tm.rankme.cqrs.command.game

import com.tm.rankme.cqrs.command.CommandHandler
import com.tm.rankme.domain.game.GameRepository
import com.tm.rankme.domain.game.PlayerPort
import org.springframework.stereotype.Service

@Service
class CompleteGameHandler(
    private val repository: GameRepository,
    private val playerPort: PlayerPort
) : CommandHandler<CompleteGameCommand> {

    override fun dispatch(command: CompleteGameCommand) {
        val game = repository.byId(command.gameId)
        val playedGame = playerPort.playGame(
            game.playerIds.first, game.playerIds.second,
            command.playerOneScore, command.playerTwoScore
        )
        game.complete(playedGame.result!!.first, playedGame.result!!.second)
        repository.store(game)
    }
}