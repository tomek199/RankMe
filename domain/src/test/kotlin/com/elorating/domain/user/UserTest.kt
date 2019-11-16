package com.elorating.domain.user

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class UserTest {
    @Test
    internal fun shouldCreateUserWithDefaultParameters() {
        // given
        val username = "Optimus Prime"
        // when
        val user = User(username)
        // then
        assertEquals(username, user.username)
        assertTrue(user.leagues.isEmpty())
    }
}