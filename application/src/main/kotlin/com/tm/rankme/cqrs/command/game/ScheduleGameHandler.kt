package com.tm.rankme.cqrs.command.game

import com.tm.rankme.cqrs.command.CommandHandler
import com.tm.rankme.domain.game.Game
import com.tm.rankme.domain.game.GameRepository
import com.tm.rankme.domain.game.PlayerPort
import org.springframework.stereotype.Service

@Service
class ScheduleGameHandler(
    private val repository: GameRepository,
    private val playerPort: PlayerPort
) : CommandHandler<ScheduleGameCommand> {

    override fun dispatch(command: ScheduleGameCommand) {
        val leagueId = playerPort.extractLeagueId(command.playerOneId, command.playerTwoId)
        val game = Game.scheduled(leagueId, command.dateTime, command.playerOneId, command.playerTwoId)
        repository.store(game)
    }
}