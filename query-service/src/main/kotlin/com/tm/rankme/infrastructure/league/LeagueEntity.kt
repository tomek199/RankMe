package com.tm.rankme.infrastructure.league

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "league")
class LeagueEntity(
    @Id val id: String,
    var name: String,
    var allowDraws: Boolean,
    var maxScore: Int
)