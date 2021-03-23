package com.tm.rankme.command.game

import com.tm.rankme.command.CommandHandler
import com.tm.rankme.domain.base.Event
import com.tm.rankme.domain.base.EventBus
import com.tm.rankme.domain.game.Game
import com.tm.rankme.domain.game.GameRepository
import com.tm.rankme.domain.game.PlayerPort
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Service
import java.util.function.Consumer

@Service
class ScheduleGameHandler(
    private val repository: GameRepository,
    private val playerPort: PlayerPort,
    eventBus: EventBus
) : CommandHandler<ScheduleGameCommand>(eventBus) {

    @Bean("scheduleGameCommandHandler")
    override fun dispatch(): Consumer<ScheduleGameCommand> = super.dispatch()

    override fun execute(command: ScheduleGameCommand): List<Event<Game>> {
        val leagueId = playerPort.extractLeagueId(command.playerOneId, command.playerTwoId)
        val game = Game.scheduled(leagueId, command.dateTime, command.playerOneId, command.playerTwoId)
        repository.store(game)
        return game.pendingEvents
    }
}