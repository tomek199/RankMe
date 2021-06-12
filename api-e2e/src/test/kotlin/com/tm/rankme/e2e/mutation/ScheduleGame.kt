package com.tm.rankme.e2e.mutation

import com.expediagroup.graphql.client.types.GraphQLClientRequest
import java.time.LocalDateTime
import java.util.*
import kotlin.reflect.KClass

class ScheduleGame(
    playerOneId: UUID,
    playerTwoId: UUID,
    dateTime: LocalDateTime
) : GraphQLClientRequest<ScheduleGame.Result> {

    override val query: String =
        """mutation {
            scheduleGame(command: {
                playerOneId: "$playerOneId", 
                playerTwoId: "$playerTwoId",
                dateTime: "$dateTime"
            })
        }"""

    override fun responseType(): KClass<Result> = Result::class

    data class Result(val scheduleGame: String)
}