package com.tm.rankme.cqrs.command.league

import com.tm.rankme.cqrs.command.CommandHandler
import com.tm.rankme.domain.league.LeagueRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ChangeLeagueSettingsHandler @Autowired constructor(
    private val repository: LeagueRepository
) : CommandHandler<ChangeLeagueSettingsCommand> {

    override fun dispatch(command: ChangeLeagueSettingsCommand) {
        val league = repository.byId(command.id)
        league.settings(command.allowDraws, command.maxScore)
        repository.store(league)
    }
}