package com.tm.rankme.storage.read.player

import java.util.*
import org.springframework.data.mongodb.repository.MongoRepository

interface MongoPlayerAccessor : MongoRepository<PlayerEntity, UUID>