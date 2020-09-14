package com.tm.rankme.application.game

import graphql.relay.Connection
import graphql.schema.DataFetchingEnvironment

interface GameService {
    fun get(gameId: String): GameModel
    fun create(
        leagueId: String,
        firstCompetitorId: String, firstScore: Int,
        secondCompetitorId: String, secondScore: Int
    ): GameModel

    fun complete(eventId: String, playerOneScore: Int, playerTwoScore: Int): GameModel
    fun getConnectionForLeague(
        leagueId: String, first: Int,
        after: String? = null, env: DataFetchingEnvironment
    ): Connection<GameModel>
}