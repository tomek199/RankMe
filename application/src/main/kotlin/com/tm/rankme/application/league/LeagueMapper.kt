package com.tm.rankme.application.league

import com.tm.rankme.application.Mapper
import com.tm.rankme.domain.league.League
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
@Qualifier("leagueMapper")
class LeagueMapper : Mapper<League, LeagueModel> {
    override fun toDomain(model: LeagueModel): League {
        val league = League(model.id, model.name)
        league.setAllowDraws(model.settings.allowDraws)
        league.setMaxScore(model.settings.maxScore)
        return league
    }

    override fun toModel(domain: League): LeagueModel {
        val id = domain.id?: throw IllegalStateException("League id can't be null")
        val settings = LeagueSettingsModel(domain.settings.allowDraws, domain.settings.maxScore)
        return LeagueModel(id, domain.name, settings)
    }
}