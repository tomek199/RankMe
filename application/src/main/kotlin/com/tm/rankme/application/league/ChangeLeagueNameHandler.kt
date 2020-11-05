package com.tm.rankme.application.league

import com.tm.rankme.application.cqrs.CommandHandler
import com.tm.rankme.domain.league.LeagueRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ChangeLeagueNameHandler @Autowired constructor(
    private val repository: LeagueRepository
) : CommandHandler<ChangeLeagueNameCommand> {

    override fun dispatch(command: ChangeLeagueNameCommand) {
        val league = repository.byId(command.id)
        league.name(command.name)
        repository.store(league)
    }
}