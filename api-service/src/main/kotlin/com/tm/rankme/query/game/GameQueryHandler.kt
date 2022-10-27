package com.tm.rankme.query.game

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

private const val HANDLE_QUERY_LOG = "Handle query {}"

@Service
class GameQueryHandler(
    private val restTemplate: RestTemplate,
    @Value("\${gateway.url}") private val url: String
) {

    private val log = LoggerFactory.getLogger(GameQueryHandler::class.java)

    fun handle(query: GetGamesForLeagueQuery): Connection<Game> {
        log.info(HANDLE_QUERY_LOG, query)
        var endpoint = "$url/query-service/leagues/${query.leagueId}/games?first=${query.first}"
        query.after?.let { endpoint += "&after=${query.after}" }
        query.before?.let { endpoint += "&before=${query.before}" }
        val response =  restTemplate.exchange(endpoint, HttpMethod.GET, null,
            object : ParameterizedTypeReference<Page<Game>>() {}
        )
        return response.body?.let { ConnectionBuilder(it).build() }
            ?: throw QueryException("Empty response body for GET query=$endpoint")
    }

    fun handle(query: GetCompletedGamesForLeagueQuery): Connection<CompletedGame> {
        log.info(HANDLE_QUERY_LOG, query)
        var endpoint = "$url/query-service/leagues/${query.leagueId}/completed-games?first=${query.first}"
        query.after?.let { endpoint += "&after=${query.after}" }
        query.before?.let { endpoint += "&before=${query.before}" }
        val response =  restTemplate.exchange(endpoint, HttpMethod.GET, null,
            object : ParameterizedTypeReference<Page<CompletedGame>>() {}
        )
        return response.body?.let { ConnectionBuilder(it).build() }
            ?: throw QueryException("Empty response body for GET query=$endpoint")
    }

    fun handle(query: GetScheduledGamesForLeagueQuery): Connection<ScheduledGame> {
        log.info(HANDLE_QUERY_LOG, query)
        var endpoint = "$url/query-service/leagues/${query.leagueId}/scheduled-games?first=${query.first}"
        query.after?.let { endpoint += "&after=${query.after}" }
        query.before?.let { endpoint += "&before=${query.before}" }
        val response =  restTemplate.exchange(endpoint, HttpMethod.GET, null,
            object : ParameterizedTypeReference<Page<ScheduledGame>>() {}
        )
        return response.body?.let { ConnectionBuilder(it).build() }
            ?: throw QueryException("Empty response body for GET query=$endpoint")
    }

    fun handle(query: GetGamesForPlayerQuery): Connection<Game> {
        log.info(HANDLE_QUERY_LOG, query)
        var endpoint = "$url/query-service/players/${query.playerId}/games?first=${query.first}"
        query.after?.let { endpoint += "&after=$it" }
        query.before?.let { endpoint += "&before=${query.before}" }
        val response =  restTemplate.exchange(endpoint, HttpMethod.GET, null,
            object : ParameterizedTypeReference<Page<Game>>() {}
        )
        return response.body?.let { ConnectionBuilder(it).build() }
            ?: throw QueryException("Empty response body for GET query=$endpoint")
    }

    fun handle(query: GetCompletedGamesForPlayerQuery): Connection<CompletedGame> {
        log.info(HANDLE_QUERY_LOG, query)
        var endpoint = "$url/query-service/players/${query.playerId}/completed-games?first=${query.first}"
        query.after?.let { endpoint += "&after=$it" }
        query.before?.let { endpoint += "&before=${query.before}" }
        val response =  restTemplate.exchange(endpoint, HttpMethod.GET, null,
            object : ParameterizedTypeReference<Page<CompletedGame>>() {}
        )
        return response.body?.let { ConnectionBuilder(it).build() }
            ?: throw QueryException("Empty response body for GET query=$endpoint")
    }

    fun handle(query: GetScheduledGamesForPlayerQuery): Connection<ScheduledGame> {
        log.info(HANDLE_QUERY_LOG, query)
        var endpoint = "$url/query-service/players/${query.playerId}/scheduled-games?first=${query.first}"
        query.after?.let { endpoint += "&after=$it" }
        query.before?.let { endpoint += "&before=${query.before}" }
        val response =  restTemplate.exchange(endpoint, HttpMethod.GET, null,
            object : ParameterizedTypeReference<Page<ScheduledGame>>() {}
        )
        return response.body?.let { ConnectionBuilder(it).build() }
            ?: throw QueryException("Empty response body for GET query=$endpoint")
    }
}