package com.tm.rankme.application.league

import com.tm.rankme.domain.league.League
import com.tm.rankme.domain.league.LeagueRepository
import org.springframework.stereotype.Service

@Service
internal class LeagueServiceImpl(
    private val repository: LeagueRepository
) : LeagueService {

    override fun getLeague(leagueId: String): League {
        val league = repository.findById(leagueId)
        return league ?: throw IllegalStateException("League $leagueId is not found")
    }

    override fun saveLeague(league: League): League {
        return repository.save(league)
    }

    override fun checkIfExist(leagueId: String) {
        getLeague(leagueId)
    }
}