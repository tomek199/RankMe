package com.tm.rankme.cqrs.command.league

import com.tm.rankme.cqrs.command.CommandHandler
import com.tm.rankme.domain.league.LeagueRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class RenameLeagueHandler @Autowired constructor(
    private val repository: LeagueRepository
) : CommandHandler<RenameLeagueCommand> {

    override fun dispatch(command: RenameLeagueCommand) {
        val league = repository.byId(command.id)
        league.rename(command.name)
        repository.store(league)
    }
}