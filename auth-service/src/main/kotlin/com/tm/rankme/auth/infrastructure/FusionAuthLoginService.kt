package com.tm.rankme.auth.infrastructure

import com.tm.rankme.auth.application.LoginService
import com.tm.rankme.auth.application.Token
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate

@Service
class FusionAuthLoginService(
    @Value("\${gateway.url}") private val url: String,
    @Value("\${application.id}") private val applicationId: String,
    @Value("\${application.key}") private val applicationKey: String,
    private val restTemplate: RestTemplate
) : LoginService {

    private val log = LoggerFactory.getLogger(FusionAuthLoginService::class.java)

    override fun login(login: String, password: String) = response(request(login, password))

    private fun request(login: String, password: String): HttpEntity<Request> {
        val headers = HttpHeaders()
        headers.add("Authorization", applicationKey)
        return HttpEntity(Request(applicationId, login, password), headers)
    }

    private fun response(request: HttpEntity<Request>): ResponseEntity<Token?> {
        return try {
            restTemplate.postForEntity(
                "$url/auth-server/login",
                request,
                Response::class.java
            ).body?.let { body -> ResponseEntity.ok(Token(body.token, body.refreshToken)) } ?: ResponseEntity.notFound().build()
        } catch (e: HttpClientErrorException) {
            log.info("Login failed for user ${request.body?.loginId}. ${e.message}")
            ResponseEntity.notFound().build()
        }
    }

    data class Request(
        val applicationId: String,
        val loginId: String,
        val password: String
    )

    data class Response(
        val token: String,
        val refreshToken: String
    )
}