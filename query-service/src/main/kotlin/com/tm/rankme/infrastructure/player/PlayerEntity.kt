package com.tm.rankme.infrastructure.player

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "player")
data class PlayerEntity(
    @Id val id: String,
    val leagueId: String,
    var name: String,
    var deviation: Int,
    var rating: Int
)