package com.tm.rankme.application.event

import java.time.LocalDateTime

data class EventModel(
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

data class AddEventInput(
    val leagueId: String,
    val memberOneId: String,
    val memberTwoId: String,
    val dateTime: LocalDateTime
)
