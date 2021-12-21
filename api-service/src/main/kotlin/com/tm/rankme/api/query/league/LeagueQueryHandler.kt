package com.tm.rankme.api.query.league

import com.tm.rankme.api.query.Page
import com.tm.rankme.api.query.QueryException
import graphql.relay.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class LeagueQueryHandler(
    private val restTemplate: RestTemplate,
    @Value("\${gateway.url}") private val url: String
) {

    fun handle(query: GetLeagueQuery): League? {
        return restTemplate.getForObject("$url/query-service/leagues/${query.id}", League::class.java)
    }

    fun handle(query: GetLeaguesQuery): Connection<League> {
        var endpoint = "$url/query-service/leagues?first=${query.first}"
        query.after?.let { endpoint += "&after=$it" }
        return request(endpoint)
    }

    private fun request(endpoint: String): Connection<League> {
        val response = restTemplate.exchange(endpoint, HttpMethod.GET, null,
            object : ParameterizedTypeReference<Page<League>>() {}
        )
        return response.body?.let { DefaultConnection(edges(it), pageInfo(it)) }
            ?: throw QueryException("Empty response body for GET query=$endpoint")
    }

    private fun edges(page: Page<League>): List<Edge<League>> = page.items.map {
        DefaultEdge(it.node, DefaultConnectionCursor(it.cursor))
    }

    private fun pageInfo(page: Page<League>): PageInfo = DefaultPageInfo(
        DefaultConnectionCursor(page.items.first().cursor),
        DefaultConnectionCursor(page.items.last().cursor),
        page.hasPreviousPage, page.hasNextPage
    )
}