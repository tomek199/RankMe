package com.tm.rankme.api.query.player

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class PlayerQueryHandler(
    private val restTemplate: RestTemplate,
    @Value("\${gateway.url}") private val url: String
) {

    private val log = LoggerFactory.getLogger(PlayerQueryHandler::class.java)

    fun handle(query: GetPlayerQuery): Player? {
        log.info("Handle query {}", query)
        return restTemplate.getForObject("$url/query-service/players/${query.id}", Player::class.java)
    }

    fun handle(query: GetPlayersQuery): List<Player> {
        log.info("Handle query {}", query)
        val response = restTemplate.exchange(
            "$url/query-service/leagues/${query.leagueId}/players",
            HttpMethod.GET, null,
            object : ParameterizedTypeReference<List<Player>>() {}
        )
        return response.body ?: emptyList()
    }
}