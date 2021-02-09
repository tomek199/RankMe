package com.tm.rankme.infrastructure.player

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.tm.rankme.domain.base.Event
import com.tm.rankme.domain.player.Player
import com.tm.rankme.domain.player.PlayerCreated
import com.tm.rankme.domain.player.PlayerPlayedGame
import com.tm.rankme.infrastructure.InfrastructureException
import org.springframework.stereotype.Service

@Service
class PlayerMapper {
    protected val objectMapper: ObjectMapper = jacksonObjectMapper()

    fun serialize(event: Event<Player>): Any {
        return when (event) {
            is PlayerCreated -> Created(
                event.type, event.aggregateId, event.version, event.timestamp,
                event.leagueId, event.name, event.deviation, event.rating
            )
            is PlayerPlayedGame -> PlayedGame(
                event.type, event.aggregateId, event.version, event.timestamp,
                event.deviationDelta, event.ratingDelta, event.score
            )
            else -> throw InfrastructureException("Cannot serialize event '${event.type}'")
        }
    }

    fun deserialize(type: String, data: ByteArray): Event<Player> {
        return when (type) {
            "player-created" -> objectMapper.readValue(data, Created::class.java).let {
                PlayerCreated(it.leagueId, it.name, it.deviation, it.rating, it.aggregateId)
            }
            "player-played-game" -> objectMapper.readValue(data, PlayedGame::class.java).let {
                PlayerPlayedGame(it.deviationDelta, it.ratingDelta, it.score, it.aggregateId, it.version)
            }
            else -> throw InfrastructureException("Cannot deserialize event '$type'")
        }
    }
}