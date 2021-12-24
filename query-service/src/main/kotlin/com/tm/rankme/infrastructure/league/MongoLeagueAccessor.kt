package com.tm.rankme.infrastructure.league

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository

interface MongoLeagueAccessor : MongoRepository<LeagueEntity, String> {
    fun getAllByOrderByTimestampAsc(
        pageable: Pageable
    ) : Page<LeagueEntity>

    fun getByTimestampGreaterThanOrderByTimestampAsc(
        timestamp: Long, pageable: Pageable
    ) : Page<LeagueEntity>
}