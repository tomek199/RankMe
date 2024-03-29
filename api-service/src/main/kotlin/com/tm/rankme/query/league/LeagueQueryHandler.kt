package com.tm.rankme.query.league

import com.tm.rankme.query.ConnectionBuilder
import com.tm.rankme.query.Page
import com.tm.rankme.query.QueryException
import graphql.relay.Connection
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
        query.before?.let { endpoint += "&before=$it" }
        val response = restTemplate.exchange(endpoint, HttpMethod.GET, null,
            object : ParameterizedTypeReference<Page<League>>() {}
        )
        return response.body?.let { ConnectionBuilder(it).build() }
            ?: throw QueryException("Empty response body for GET query=$endpoint")
    }
}