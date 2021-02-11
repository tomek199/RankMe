package com.tm.rankme.infrastructure.game

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.tm.rankme.domain.base.Event
import com.tm.rankme.domain.game.Game
import com.tm.rankme.domain.game.GamePlayed
import com.tm.rankme.domain.game.GameScheduled
import com.tm.rankme.infrastructure.InfrastructureException
import org.springframework.stereotype.Service

@Service
class GameMapper {
    protected val objectMapper: ObjectMapper = jacksonObjectMapper()

    fun serialize(event: Event<Game>): Any {
        return when (event) {
            is GamePlayed -> Played(
                event.type, event.aggregateId, event.version, event.timestamp, event.leagueId, event.dateTime,
                event.firstId, event.firstScore, event.firstDeviationDelta, event.firstRatingDelta,
                event.secondId, event.secondScore, event.secondDeviationDelta, event.secondRatingDelta
            )
            is GameScheduled -> Scheduled(
                event.type, event.aggregateId, event.version, event.timestamp, event.leagueId,
                event.dateTime, event.firstId, event.secondId
            )
            else -> throw InfrastructureException("Cannot serialize event '${event.type}'")
        }
    }

    fun deserialize(type: String, data: String) = deserialize(type, data.toByteArray())

    fun deserialize(type: String, data: ByteArray): Event<Game> {
        return when (type) {
            "game-scheduled" -> objectMapper.readValue(data, Scheduled::class.java).let {
                GameScheduled(it.leagueId, it.firstId, it.secondId, it.dateTime, it.aggregateId)
            }
            "game-played" -> objectMapper.readValue(data, Played::class.java).let {
                GamePlayed(
                    it.leagueId, it.firstId, it.firstScore, it.firstDeviationDelta, it.firstRatingDelta,
                    it.secondId, it.secondScore, it.secondDeviationDelta, it.secondRatingDelta,
                    it.dateTime, it.aggregateId, it.version
                )
            }
            else -> throw InfrastructureException("Cannot deserialize event '${type}'")
        }
    }
}