package com.tm.rankme.infrastructure

import java.util.*
import org.springframework.data.mongodb.repository.MongoRepository

interface MongoLeagueAccessor : MongoRepository<LeagueEntity, UUID>