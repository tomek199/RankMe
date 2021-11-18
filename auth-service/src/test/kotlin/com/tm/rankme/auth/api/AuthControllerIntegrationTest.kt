package com.tm.rankme.auth.api

import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.junit5.WireMockTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = ["test"])
@AutoConfigureMockMvc
@WireMockTest(httpPort = 9700)
internal class AuthControllerIntegrationTest {
    @Autowired
    private lateinit var mvc: MockMvc

    @Test
    internal fun `Should return token`() {
        // given
        stubFor(
            post("/auth-server/login")
                .withHeader("Authorization", equalTo("app-key"))
                .withRequestBody(equalTo("""{"applicationId":"app-id","loginId":"bumblebee","password":"1234abcd"}"""))
            .willReturn(
                okJson("""{"token": "token-value", "refreshToken": "refresh-token-value"}"""))
        )
        // when
        val result = mvc.post("/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"login": "bumblebee", "password": "1234abcd"}"""
        }
        // then
        result.andExpect {
            status { isOk() }
            jsonPath("$.accessToken") { value("token-value") }
            jsonPath("$.refreshToken") { value("refresh-token-value") }
        }
    }

    @Test
    internal fun `Should return not found when login failed`() {
        // given
        stubFor(
            post("/auth-server/login")
                .withHeader("Authorization", equalTo("app-key"))
                .withRequestBody(equalTo("""{"applicationId":"app-id","loginId":"bumblebee","password":"1234abcd"}"""))
            .willReturn(notFound())
        )
        // when
        val result = mvc.post("/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"login": "bumblebee", "password": "1234abcd"}"""
        }
        // then
        result.andExpect {
            status { isNotFound() }
        }
    }
}