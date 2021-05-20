package com.tm.rankme.infrastructure.game

import org.springframework.data.mongodb.repository.MongoRepository
import java.util.*

interface MongoGameAccessor : MongoRepository<GameEntity, UUID>