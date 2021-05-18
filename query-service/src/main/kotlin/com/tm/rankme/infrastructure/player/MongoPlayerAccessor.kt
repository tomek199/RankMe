package com.tm.rankme.infrastructure.player

import java.util.*
import org.springframework.data.mongodb.repository.MongoRepository

interface MongoPlayerAccessor : MongoRepository<PlayerEntity, UUID>