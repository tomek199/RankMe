package com.tm.rankme.api.query.player

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

    fun handle(query: GetPlayerQuery): Player? {
        return restTemplate.getForObject("$url/query-service/players/${query.id}", Player::class.java)
    }

    fun handle(query: GetPlayersQuery): List<Player> {
        val response = restTemplate.exchange(
            "$url/query-service/players?leagueId=${query.leagueId}",
            HttpMethod.GET, null,
            object : ParameterizedTypeReference<List<Player>>() {}
        )
        return response.body ?: emptyList()
    }
}