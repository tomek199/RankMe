package com.tm.rankme.api.query.game

import com.tm.rankme.api.query.Page
import com.tm.rankme.api.query.QueryException
import graphql.relay.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class GameQueryHandler(
    private val restTemplate: RestTemplate,
    @Value("\${gateway.url}") private val url: String
) {

    fun handle(query: GetGamesQuery): Connection<Game> {
        var endpoint = "$url/query-service/leagues/${query.leagueId}/games?first=${query.first}"
        query.after?.let { endpoint += "&after=${query.after}" }
        val response = restTemplate.exchange(endpoint, HttpMethod.GET, null,
            object : ParameterizedTypeReference<Page<Game>>() {}
        )
        return response.body?.let { DefaultConnection(edges(it), pageInfo(it)) }
            ?: throw QueryException("Empty response body for query=$query")
    }

    private fun edges(page: Page<Game>): List<Edge<Game>> = page.items.map {
        DefaultEdge(it.node, DefaultConnectionCursor(it.cursor))
    }

    private fun pageInfo(page: Page<Game>): PageInfo = DefaultPageInfo(
            DefaultConnectionCursor(page.items.first().cursor),
            DefaultConnectionCursor(page.items.last().cursor),
            page.hasPreviousPage, page.hasNextPage
    )
}