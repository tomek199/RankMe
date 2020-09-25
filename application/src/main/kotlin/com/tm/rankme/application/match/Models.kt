package com.tm.rankme.application.match

import java.time.LocalDateTime

data class MatchModel(
    val id: String,
    val memberOne: MemberModel,
    val memberTwo: MemberModel,
    val dateTime: LocalDateTime
)

data class MemberModel(
    val competitorId: String,
    val username: String,
    val deviation: Int,
    val rating: Int
)

data class AddMatchInput(
    val leagueId: String,
    val memberOneId: String,
    val memberTwoId: String,
    val dateTime: LocalDateTime
)
