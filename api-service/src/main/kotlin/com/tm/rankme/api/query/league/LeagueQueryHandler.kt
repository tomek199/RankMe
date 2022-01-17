package com.tm.rankme.api.query.league

import com.tm.rankme.api.query.Page
import com.tm.rankme.api.query.QueryException
import graphql.relay.*
import org.slf4j.LoggerFactory
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

    private val log = LoggerFactory.getLogger(LeagueQueryHandler::class.java)

    fun handle(query: GetLeagueQuery): League? {
        log.info("Handle query {}", query)
        return restTemplate.getForObject("$url/query-service/leagues/${query.id}", League::class.java)
    }

    fun handle(query: GetLeaguesQuery): Connection<League> {
        log.info("Handle query {}", query)
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
        if (page.items.isNotEmpty()) DefaultConnectionCursor(page.items.first().cursor) else null,
        if (page.items.isNotEmpty()) DefaultConnectionCursor(page.items.last().cursor) else null,
        page.hasPreviousPage, page.hasNextPage
    )
}