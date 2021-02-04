package com.tm.rankme.cqrs.command.game

import com.tm.rankme.cqrs.command.CommandHandler
import com.tm.rankme.domain.base.AggregateException
import com.tm.rankme.domain.base.Event
import com.tm.rankme.domain.base.EventBus
import com.tm.rankme.domain.game.Game
import com.tm.rankme.domain.game.GamePlayed
import com.tm.rankme.domain.game.GameRepository
import com.tm.rankme.domain.game.GameScheduled
import com.tm.rankme.domain.game.PlayerPort
import com.tm.rankme.domain.game.Result
import com.tm.rankme.domain.league.League
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import io.mockk.verifySequence
import java.time.Instant
import java.time.LocalDateTime
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test

internal class CompleteGameHandlerTest {
    private val repository = mockk<GameRepository>()
    private val playerPort = mockk<PlayerPort>()
    private val eventBus = mockk<EventBus>()
    private val handler: CommandHandler<CompleteGameCommand> = CompleteGameHandler(repository, playerPort, eventBus)

    @Test
    internal fun `Should create played game`() {
        // given
        val command = CompleteGameCommand(UUID.randomUUID(), 3, 2)
        val leagueId = UUID.randomUUID()
        val firstResult = Result(command.playerOneScore, -34, -85)
        val secondResult = Result(command.playerTwoScore, -45, 79)
        val game = Game.from(listOf(
            GameScheduled(leagueId, UUID.randomUUID(), UUID.randomUUID(), Instant.now().toEpochMilli(), UUID.randomUUID())
        ))
        every { repository.byId(command.gameId) } returns game
        every { repository.store(any()) } just Runs
        every {
            playerPort.playGame(game.playerIds.first, game.playerIds.second, command.playerOneScore, command.playerTwoScore)
        } returns Game.played(leagueId, game.playerIds.first, game.playerIds.second, firstResult, secondResult)
        every { eventBus.emit(any()) } just Runs
        // when
        handler.dispatch(command)
        // then
        val gameSlot = slot<Game>()
        verifySequence {
            repository.byId(command.gameId)
            repository.store(capture(gameSlot))
        }
        verify(exactly = 1) {
            playerPort.playGame(game.playerIds.first, game.playerIds.second, command.playerOneScore, command.playerTwoScore)
        }
        verify(exactly = 1) { eventBus.emit(ofType<Event<League>>()) }
        gameSlot.captured.let {
            assertEquals(leagueId, it.leagueId)
            assertEquals(Pair(game.playerIds.first, game.playerIds.second), it.playerIds)
            assertEquals(Pair(firstResult, secondResult), it.result)
            assertNotNull(it.dateTime)
            assertTrue(it.pendingEvents.last() is GamePlayed)
        }
    }

    @Test
    internal fun `Should throw exception when try to complete already played game`() {
        // given
        val command = CompleteGameCommand(UUID.randomUUID(), 3, 4)
        val firstResult = Result(command.playerOneScore, -34, -85)
        val secondResult = Result(command.playerTwoScore, -45, 79)
        val game = Game.played(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), firstResult, secondResult)
        every { repository.byId(command.gameId) } returns game
        // when
        val exception = assertFailsWith<AggregateException> { handler.dispatch(command) }
        // then
        assertEquals("Game ${game.id} is already played", exception.message)
    }

    @Test
    internal fun `Should throw exception when port throws exception`() {
        // given
        val game = Game.scheduled(UUID.randomUUID(), LocalDateTime.now(), UUID.randomUUID(), UUID.randomUUID())
        val command = CompleteGameCommand(game.id, 3, 2)
        every { repository.byId(command.gameId) } returns game
        every {
            playerPort.playGame(ofType(UUID::class), ofType(UUID::class), command.playerOneScore, command.playerTwoScore)
        } throws AggregateException("Cannot complete game")
        // when
        val exception = assertFailsWith<AggregateException> { (handler.dispatch(command)) }
        // then
        assertEquals("Cannot complete game", exception.message)
    }
}