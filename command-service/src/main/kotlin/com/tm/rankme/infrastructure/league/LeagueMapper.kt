package com.tm.rankme.infrastructure.league

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.tm.rankme.domain.base.Event
import com.tm.rankme.domain.league.League
import com.tm.rankme.domain.league.LeagueCreated
import com.tm.rankme.domain.league.LeagueRenamed
import com.tm.rankme.domain.league.LeagueSettingsChanged
import com.tm.rankme.infrastructure.InfrastructureException
import org.springframework.stereotype.Service

@Service
class LeagueMapper {
    protected val objectMapper: ObjectMapper = jacksonObjectMapper()

    fun serialize(event: Event<League>): Any {
        return when (event) {
            is LeagueCreated -> Created(
                event.type, event.aggregateId, event.version, event.timestamp,
                event.name, event.allowDraws, event.maxScore
            )
            is LeagueRenamed -> Renamed(
                event.type, event.aggregateId, event.version, event.timestamp,
                event.name
            )
            is LeagueSettingsChanged -> SettingsChanged(
                event.type, event.aggregateId, event.version, event.timestamp,
                event.allowDraws, event.maxScore
            )
            else -> throw InfrastructureException("Cannot serialize event '${event.type}'")
        }
    }

    fun deserialize(type: String, data: ByteArray): Event<League> {
        return when (type) {
            "league-created" -> objectMapper.readValue(data, Created::class.java).let {
                LeagueCreated(it.name, it.allowDraws, it.maxScore, it.aggregateId)
            }
            "league-renamed" -> objectMapper.readValue(data, Renamed::class.java).let {
                LeagueRenamed(it.aggregateId, it.version, it.name)
            }
            "league-settings-changed" -> objectMapper.readValue(data, SettingsChanged::class.java)
                .let {
                    LeagueSettingsChanged(it.aggregateId, it.version, it.allowDraws, it.maxScore)
                }
            else -> throw InfrastructureException("Cannot deserialize event '$type'")
        }
    }
}