package com.tm.rankme.api.mutation

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.only
import com.nhaarman.mockitokotlin2.verify
import com.tm.rankme.application.league.CreateLeagueCommand
import kotlin.test.assertEquals
import kotlin.test.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class MutationTest {
    private val executor: CommandExecutor = mock()
    private val mutation = Mutation(executor)

    @BeforeEach
    internal fun setUp() {
        given(executor.execute(any())).willReturn(Result())
    }

    @Test
    internal fun `Should execute "create league" command`() {
        // given
        val command = CreateLeagueCommand("Start Wars")
        // when
        val result: Result = mutation.createLeague(command)
        // then
        verify(executor, only()).execute(command)
        assertEquals(Status.SUCCESS, result.status)
        assertNull(result.message)
    }
}