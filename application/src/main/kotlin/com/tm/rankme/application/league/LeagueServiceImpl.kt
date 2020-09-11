package com.tm.rankme.application.league

import com.tm.rankme.application.common.Mapper
import com.tm.rankme.domain.league.League
import com.tm.rankme.domain.league.LeagueRepository
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
internal class LeagueServiceImpl(
    private val repository: LeagueRepository,
    @Qualifier("leagueMapper") private val mapper: Mapper<League, LeagueModel>
) : LeagueService {

    override fun get(leagueId: String): League {
        val league = repository.findById(leagueId)
        return league ?: throw IllegalStateException("League $leagueId is not found")
    }

    override fun create(name: String): LeagueModel {
        val league = repository.save(League(name))
        return mapper.toModel(league)
    }

    override fun checkIfExist(leagueId: String) {
        get(leagueId)
    }
}