package com.tm.rankme.command.league

import com.tm.rankme.command.CommandHandler
import com.tm.rankme.domain.base.Event
import com.tm.rankme.domain.base.EventBus
import com.tm.rankme.domain.league.League
import com.tm.rankme.domain.league.LeagueRepository
import org.springframework.stereotype.Service
import java.util.function.Consumer

@Service("changeLeagueSettingsCommandHandler")
class ChangeLeagueSettingsHandler(
    private val repository: LeagueRepository,
    eventBus: EventBus
) : CommandHandler<ChangeLeagueSettingsCommand>(eventBus), Consumer<ChangeLeagueSettingsCommand> {

    override fun accept(command: ChangeLeagueSettingsCommand) = dispatch(command)

    override fun execute(command: ChangeLeagueSettingsCommand): List<Event<League>> {
        val league = repository.byId(command.id)
        league.settings(command.allowDraws, command.maxScore)
        repository.store(league)
        return league.pendingEvents
    }
}