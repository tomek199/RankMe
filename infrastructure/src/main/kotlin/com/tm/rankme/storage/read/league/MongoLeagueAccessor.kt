package com.tm.rankme.storage.read.league

import java.util.*
import org.springframework.data.mongodb.repository.MongoRepository

interface MongoLeagueAccessor : MongoRepository<LeagueEntity, UUID>