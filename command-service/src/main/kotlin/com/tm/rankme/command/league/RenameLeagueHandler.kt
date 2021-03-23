package com.tm.rankme.command.league

import com.tm.rankme.command.CommandHandler
import com.tm.rankme.domain.base.Event
import com.tm.rankme.domain.base.EventBus
import com.tm.rankme.domain.league.League
import com.tm.rankme.domain.league.LeagueRepository
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Service
import java.util.function.Consumer

@Service
class RenameLeagueHandler(
    private val repository: LeagueRepository,
    eventBus: EventBus
) : CommandHandler<RenameLeagueCommand>(eventBus) {

    @Bean("renameLeagueCommandHandler")
    override fun dispatch(): Consumer<RenameLeagueCommand> = super.dispatch()

    override fun execute(command: RenameLeagueCommand): List<Event<League>> {
        val league = repository.byId(command.id)
        league.rename(command.name)
        repository.store(league)
        return league.pendingEvents
    }
}