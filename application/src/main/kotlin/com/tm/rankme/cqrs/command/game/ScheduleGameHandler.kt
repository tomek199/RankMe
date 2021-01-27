package com.tm.rankme.cqrs.command.game

import com.tm.rankme.cqrs.command.CommandHandler
import com.tm.rankme.domain.base.Event
import com.tm.rankme.domain.base.EventBus
import com.tm.rankme.domain.game.Game
import com.tm.rankme.domain.game.GameRepository
import com.tm.rankme.domain.game.PlayerPort
import org.springframework.stereotype.Service

@Service
class ScheduleGameHandler(
    private val repository: GameRepository,
    private val playerPort: PlayerPort,
    eventBus: EventBus
) : CommandHandler<ScheduleGameCommand>(eventBus) {

    override fun execute(command: ScheduleGameCommand): List<Event<Game>> {
        val leagueId = playerPort.extractLeagueId(command.playerOneId, command.playerTwoId)
        val game = Game.scheduled(leagueId, command.dateTime, command.playerOneId, command.playerTwoId)
        repository.store(game)
        return game.pendingEvents
    }
}