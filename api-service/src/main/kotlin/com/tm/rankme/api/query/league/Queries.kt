package com.tm.rankme.api.query.league

data class GetLeagueQuery(
    val id: String
)

data class GetLeaguesQuery(
    val first: Int,
    val after: String? = null,
    val before: String? = null
)
