package com.tm.rankme.infrastructure.game

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository

interface MongoGameAccessor : MongoRepository<GameEntity, String> {
    fun getByLeagueIdOrderByTimestamp(
        leagueId: String, pageable: Pageable
    ): Page<GameEntity>

    fun getByLeagueIdAndTimestampGreaterThanOrderByTimestamp(
        leagueId: String, timestamp: Long, pageable: Pageable
    ): Page<GameEntity>
}