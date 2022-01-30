package com.tm.rankme.api.query

import com.graphql.spring.boot.test.GraphQLResponse
import com.tm.rankme.api.query.game.CompletedGame
import com.tm.rankme.api.query.game.ScheduledGame
import kotlin.test.assertEquals

fun assertCompletedGame(game: CompletedGame, response: GraphQLResponse, prefix: String) {
    assertEquals(game.id, response.get("$prefix.cursor"))
    assertEquals(game.id, response.get("$prefix.node.id"))
    assertEquals(game.dateTime.toString(), response.get("$prefix.node.dateTime"))
    assertEquals(game.playerOneId, response.get("$prefix.node.playerOneId"))
    assertEquals(game.playerOneName, response.get("$prefix.node.playerOneName"))
    assertEquals(game.playerOneDeviation, response.get("$prefix.node.playerOneDeviation", Int::class.java))
    assertEquals(game.playerOneRating, response.get("$prefix.node.playerOneRating", Int::class.java))
    assertEquals(game.playerTwoId, response.get("$prefix.node.playerTwoId"))
    assertEquals(game.playerTwoName, response.get("$prefix.node.playerTwoName"))
    assertEquals(game.playerTwoDeviation, response.get("$prefix.node.playerTwoDeviation", Int::class.java))
    assertEquals(game.playerTwoRating, response.get("$prefix.node.playerTwoRating", Int::class.java))
    assertEquals(game.result.playerOneScore, response.get("$prefix.node.result.playerOneScore", Int::class.java))
    assertEquals(game.result.playerOneDeviationDelta, response.get("$prefix.node.result.playerOneDeviationDelta", Int::class.java))
    assertEquals(game.result.playerOneRatingDelta, response.get("$prefix.node.result.playerOneRatingDelta", Int::class.java))
    assertEquals(game.result.playerTwoScore, response.get("$prefix.node.result.playerTwoScore", Int::class.java))
    assertEquals(game.result.playerTwoDeviationDelta, response.get("$prefix.node.result.playerTwoDeviationDelta", Int::class.java))
    assertEquals(game.result.playerTwoRatingDelta, response.get("$prefix.node.result.playerTwoRatingDelta", Int::class.java))
}

fun assertScheduledGame(game: ScheduledGame, response: GraphQLResponse, prefix: String) {
    assertEquals(game.id, response.get("$prefix.cursor"))
    assertEquals(game.id, response.get("$prefix.node.id"))
    assertEquals(game.dateTime.toString(), response.get("$prefix.node.dateTime"))
    assertEquals(game.playerOneId, response.get("$prefix.node.playerOneId"))
    assertEquals(game.playerOneName, response.get("$prefix.node.playerOneName"))
    assertEquals(game.playerOneDeviation, response.get("$prefix.node.playerOneDeviation", Int::class.java))
    assertEquals(game.playerOneRating, response.get("$prefix.node.playerOneRating", Int::class.java))
    assertEquals(game.playerTwoId, response.get("$prefix.node.playerTwoId"))
    assertEquals(game.playerTwoName, response.get("$prefix.node.playerTwoName"))
    assertEquals(game.playerTwoDeviation, response.get("$prefix.node.playerTwoDeviation", Int::class.java))
    assertEquals(game.playerTwoRating, response.get("$prefix.node.playerTwoRating", Int::class.java))
}