package com.tm.rankme.auth.infrastructure

import com.tm.rankme.auth.application.LoginService
import com.tm.rankme.auth.infrastructure.FusionAuthLoginService.Request
import com.tm.rankme.auth.infrastructure.FusionAuthLoginService.Response
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.fail

internal class FusionAuthLoginServiceTest {
    private val restTemplate: RestTemplate = mockk()
    private val url = "http://gateway"
    private val applicationId = "rankme1234"
    private val applicationKey = "rankmeKey1234"
    private val service: LoginService = FusionAuthLoginService(url, applicationId, applicationKey, restTemplate)

    @Test
    internal fun `Should return token response after successful login`() {
        // given
        val expectedResponse = Response("tokenValue", "refreshTokenValue")
        val request = Request(applicationKey, "chewbacca", "abcd1234")
        every {
            restTemplate.postForEntity("$url/auth-server/login", any<Request>(), Response::class.java)
        } returns ResponseEntity(expectedResponse, HttpStatus.OK)
        // when
        val response = service.login(request.loginId, request.password)
        // then
        assertEquals(HttpStatus.OK, response.statusCode)
        response.body?.let {
            assertEquals(expectedResponse.token, it.accessToken)
            assertEquals(expectedResponse.refreshToken, it.refreshToken)
        } ?: fail("Expected response body not found")
    }

    @Test
    internal fun `Should return not found when response body is empty`() {
        // given
        val request = Request(applicationKey, "chewbacca", "abcd1234")
        every {
            restTemplate.postForEntity("$url/auth-server/login", any<Request>(), Response::class.java)
        } returns ResponseEntity(null, HttpStatus.OK)
        // when
        val response = service.login(request.loginId, request.password)
        // then
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertNull(response.body)
    }

    @Test
    internal fun `Should return not found when http client throws exception`() {
        // given
        val request = Request(applicationKey, "chewbacca", "abcd1234")
        every {
            restTemplate.postForEntity("$url/auth-server/login", any<Request>(), Response::class.java)
        } throws HttpClientErrorException(HttpStatus.NOT_FOUND, "User does not exist")
        // when
        val response = service.login(request.loginId, request.password)
        // then
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertNull(response.body)
    }
}