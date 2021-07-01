package com.tm.rankme.infrastructure.game

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query

interface MongoGameAccessor : MongoRepository<GameEntity, String> {
    fun getByLeagueIdOrderByTimestamp(
        leagueId: String, pageable: Pageable
    ): Page<GameEntity>

    fun getByLeagueIdAndTimestampGreaterThanOrderByTimestamp(
        leagueId: String, timestamp: Long, pageable: Pageable
    ): Page<GameEntity>

    @Query(value = "{'\$or': [{'playerOneId': ?0}, {'playerTwoId': ?0}]}",
        sort = "{timestamp : -1}")
    fun getByPlayerIdOrderByTimestampDesc(
        playerId: String, pageable: Pageable
    ): Page<GameEntity>

    @Query(value =
            "{'\$and': [" +
                "{'\$or': [{'playerOneId': ?0}, {'playerTwoId': ?0}]}, " +
                "{'timestamp': {\$lt: ?1}}" +
            "]}",
        sort = "{timestamp : -1}")
    fun getByPlayerIdAndCursorOrderByTimestampDesc(
        playerId: String, timestamp: Long, pageable: Pageable
    ): Page<GameEntity>
}