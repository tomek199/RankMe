package com.tm.rankme.application.league

import com.tm.rankme.application.cqrs.CommandHandler
import com.tm.rankme.domain.league.League
import com.tm.rankme.domain.league.LeagueRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class CreateLeagueHandler(
    @Autowired
    private val repository: LeagueRepository
) : CommandHandler<CreateLeagueCommand> {

    override fun dispatch(command: CreateLeagueCommand) {
        val league = League.create(command.name)
        repository.store(league)
    }
}