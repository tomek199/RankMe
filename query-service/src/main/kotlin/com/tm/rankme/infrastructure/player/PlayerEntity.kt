package com.tm.rankme.infrastructure.player

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "player")
data class PlayerEntity(
    @Id val id: UUID,
    val leagueId: UUID,
    var name: String,
    var deviation: Int,
    var rating: Int
)