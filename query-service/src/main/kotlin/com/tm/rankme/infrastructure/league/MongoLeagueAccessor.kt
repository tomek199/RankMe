package com.tm.rankme.infrastructure.league

import org.springframework.data.mongodb.repository.MongoRepository

interface MongoLeagueAccessor : MongoRepository<LeagueEntity, String>