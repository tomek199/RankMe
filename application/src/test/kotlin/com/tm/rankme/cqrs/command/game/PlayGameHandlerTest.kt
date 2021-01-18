package com.tm.rankme.cqrs.command.game

import com.tm.rankme.cqrs.command.CommandHandler
import com.tm.rankme.domain.base.AggregateException
import com.tm.rankme.domain.game.Game
import com.tm.rankme.domain.game.GamePlayed
import com.tm.rankme.domain.game.GameRepository
import com.tm.rankme.domain.game.PlayerPort
import com.tm.rankme.domain.game.Result
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test

internal class PlayGameHandlerTest {
    private val repository = mockk<GameRepository>()
    private val playerPort = mockk<PlayerPort>()
    private val handler: CommandHandler<PlayGameCommand> = PlayGameHandler(repository, playerPort)

    @Test
    internal fun `Should create played game`() {
        // given
        val command = PlayGameCommand(UUID.randomUUID(), UUID.randomUUID(), 2, 5)
        val leagueId = UUID.randomUUID()
        val firstResult = Result(command.playerOneScore, -34, -85)
        val secondResult = Result(command.playerTwoScore, -45, 79)
        every {
            playerPort.playGame(command.playerOneId, command.playerTwoId, command.playerOneScore, command.playerTwoScore)
        } returns Game.played(leagueId, command.playerOneId, command.playerTwoId, firstResult, secondResult)
        every { repository.store(any()) } just Runs
        // when
        handler.dispatch(command)
        // then
        val gameSlot = slot<Game>()
        verify(exactly = 1) { repository.store(capture(gameSlot)) }
        assertEquals(leagueId, gameSlot.captured.leagueId)
        assertEquals(Pair(command.playerOneId, command.playerTwoId), gameSlot.captured.playerIds)
        assertEquals(Pair(firstResult, secondResult), gameSlot.captured.result)
        assertTrue(gameSlot.captured.pendingEvents.last() is GamePlayed)
    }

    @Test
    internal fun `Should throw exception when port throws exception`() {
        // given
        val command = PlayGameCommand(UUID.randomUUID(), UUID.randomUUID(), 3, 2)
        every {
            playerPort.playGame(command.playerOneId, command.playerTwoId, command.playerOneScore, command.playerTwoScore)
        } throws AggregateException("Cannot play game")
        // when
        val exception = assertFailsWith<AggregateException> { (handler.dispatch(command)) }
        // then
        assertEquals("Cannot play game", exception.message)
    }
}