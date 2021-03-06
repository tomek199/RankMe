package com.tm.rankme.command.league

import com.tm.rankme.command.CommandHandler
import com.tm.rankme.domain.base.Event
import com.tm.rankme.domain.base.EventBus
import com.tm.rankme.domain.league.League
import com.tm.rankme.domain.league.LeagueRepository
import org.springframework.stereotype.Service
import java.util.function.Consumer

@Service("createLeagueCommandHandler")
class CreateLeagueHandler(
    private val repository: LeagueRepository,
    eventBus: EventBus
) : CommandHandler<CreateLeagueCommand>(eventBus), Consumer<CreateLeagueCommand> {

    override fun accept(command: CreateLeagueCommand) = dispatch(command)

    override fun execute(command: CreateLeagueCommand): List<Event<League>> {
        val league = League.create(command.name)
        repository.store(league)
        return league.pendingEvents
    }
}