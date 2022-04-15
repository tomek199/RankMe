package com.tm.rankme.infrastructure.game

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query

interface MongoGameAccessor : MongoRepository<GameEntity, String> {
    fun getByLeagueIdOrderByTimestampDesc(
        leagueId: String, pageable: Pageable
    ): Page<GameEntity>

    fun getByLeagueIdAndTimestampLessThanOrderByTimestampDesc(
        leagueId: String, timestamp: Long, pageable: Pageable
    ): Page<GameEntity>

    fun getByLeagueIdAndTimestampGreaterThanOrderByTimestampAsc(
        leagueId: String, timestamp: Long, pageable: Pageable
    ): Page<GameEntity>

    fun getByLeagueIdAndResultNotNullOrderByTimestampDesc(
        leagueId: String, pageable: Pageable
    ): Page<GameEntity>

    fun getByLeagueIdAndTimestampLessThanAndResultNotNullOrderByTimestampDesc(
        leagueId: String, timestamp: Long, pageable: Pageable
    ): Page<GameEntity>

    fun getByLeagueIdAndTimestampGreaterThanAndResultNotNullOrderByTimestampAsc(
        leagueId: String, timestamp: Long, pageable: Pageable
    ): Page<GameEntity>

    fun getByLeagueIdAndResultNullOrderByTimestampDesc(
        leagueId: String, pageable: Pageable
    ): Page<GameEntity>

    fun getByLeagueIdAndTimestampLessThanAndResultNullOrderByTimestampDesc(
        leagueId: String, timestamp: Long, pageable: Pageable
    ): Page<GameEntity>

    fun getByLeagueIdAndTimestampGreaterThanAndResultNullOrderByTimestampAsc(
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
    fun getByPlayerIdAndTimestampLessThanOrderByTimestampDesc(
        playerId: String, timestamp: Long, pageable: Pageable
    ): Page<GameEntity>

    @Query(value =
            "{'\$and': [" +
                "{'\$or': [{'playerOneId': ?0}, {'playerTwoId': ?0}]}, " +
                "{'timestamp': {\$gt: ?1}}" +
            "]}",
        sort = "{timestamp : 1}")
    fun getByPlayerIdAndTimestampGreaterThanOrderByTimestampAsc(
        playerId: String, timestamp: Long, pageable: Pageable
    ): Page<GameEntity>

    @Query(value =
            "{'\$and': [" +
                "{'\$or': [{'playerOneId': ?0}, {'playerTwoId': ?0}]}," +
                "{'result' : {'\$ne' : null}}" +
            "]}",
        sort = "{timestamp : -1}")
    fun getByPlayerIdAndResultNotNullOrderByTimestampDesc(
        playerId: String, pageable: Pageable
    ): Page<GameEntity>

    @Query(value =
            "{'\$and': [" +
                "{'\$or': [{'playerOneId': ?0}, {'playerTwoId': ?0}]}, " +
                "{'timestamp': {\$lt: ?1}}," +
                "{'result' : {'\$ne' : null}}" +
            "]}",
        sort = "{timestamp : -1}")
    fun getByPlayerIdAndTimestampLessThanAndResultNotNullOrderByTimestampDesc(
        playerId: String, timestamp: Long, pageable: Pageable
    ): Page<GameEntity>

    @Query(value =
            "{'\$and': [" +
                "{'\$or': [{'playerOneId': ?0}, {'playerTwoId': ?0}]}, " +
                "{'timestamp': {\$gt: ?1}}," +
                "{'result' : {'\$ne' : null}}" +
            "]}",
        sort = "{timestamp : 1}")
    fun getByPlayerIdAndTimestampGreaterThanAndResultNotNullOrderByTimestampAsc(
        playerId: String, timestamp: Long, pageable: Pageable
    ): Page<GameEntity>

    @Query(value =
            "{'\$and': [" +
                "{'\$or': [{'playerOneId': ?0}, {'playerTwoId': ?0}]}," +
                "{'result' : null}" +
            "]}",
        sort = "{timestamp : -1}")
    fun getByPlayerIdAndResultNullOrderByTimestampDesc(
        playerId: String, pageable: Pageable
    ): Page<GameEntity>

    @Query(value =
            "{'\$and': [" +
                "{'\$or': [{'playerOneId': ?0}, {'playerTwoId': ?0}]}, " +
                "{'timestamp': {\$lt: ?1}}," +
                "{'result' : null}" +
            "]}",
        sort = "{timestamp : -1}")
    fun getByPlayerIdAndTimestampLessThanAndResultNullOrderByTimestampDesc(
        playerId: String, timestamp: Long, pageable: Pageable
    ): Page<GameEntity>

    @Query(value =
            "{'\$and': [" +
                "{'\$or': [{'playerOneId': ?0}, {'playerTwoId': ?0}]}, " +
                "{'timestamp': {\$gt: ?1}}," +
                "{'result' : null}" +
            "]}",
        sort = "{timestamp : 1}")
    fun getByPlayerIdAndTimestampGreaterThanAndResultNullOrderByTimestampAsc(
        playerId: String, timestamp: Long, pageable: Pageable
    ): Page<GameEntity>
}