package com.tm.rankme.api.query.player

import org.springframework.beans.factory.annotation.Value
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
}