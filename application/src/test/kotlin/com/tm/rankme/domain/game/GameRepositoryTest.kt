package com.tm.rankme.domain.game

import com.tm.rankme.domain.base.EventEmitter
import com.tm.rankme.domain.base.EventStorage
import io.mockk.every
import io.mockk.mockk
import io.mockk.verifySequence
import java.util.*
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test

internal class GameRepositoryTest {
    private val eventStorage = mockk<EventStorage<Game>>(relaxed = true)
    private val eventEmitter = mockk<EventEmitter>(relaxed = true)
    private val repository = object : GameRepository(eventStorage, eventEmitter) {}

    @Test
    internal fun `Should get game by id`() {
        // given
        val aggregateId = UUID.randomUUID()
        val gamePlayed = GamePlayed(UUID.randomUUID(), UUID.randomUUID(), 2, -32, -56,
            UUID.randomUUID(), 5, -42, 98, UUID.randomUUID())
        every { eventStorage.events(aggregateId.toString()) } returns listOf(gamePlayed)
        // when
        val game = repository.byId(aggregateId)
        // then
        assertEquals(gamePlayed.aggregateId, game.id)
        assertEquals(gamePlayed.version, game.version)
        assertEquals(gamePlayed.leagueId, game.leagueId)
        assertEquals(Pair(gamePlayed.firstId, gamePlayed.secondId), game.playerIds)
        assertEquals(gamePlayed.firstDeviationDelta, game.result!!.first.deviationDelta)
        assertEquals(gamePlayed.firstRatingDelta, game.result!!.first.ratingDelta)
        assertEquals(gamePlayed.firstScore, game.result!!.first.score)
        assertEquals(gamePlayed.secondDeviationDelta, game.result!!.second.deviationDelta)
        assertEquals(gamePlayed.secondRatingDelta, game.result!!.second.ratingDelta)
        assertEquals(gamePlayed.secondScore, game.result!!.second.score)
    }

    @Test
    internal fun `Should store game`() {
        // given
        val playerOneResult = Result(2, -34, 57)
        val playerTwoResult = Result(0, -45, -62)
        val game = Game.played(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), playerOneResult, playerTwoResult)
        // when
        repository.store(game)
        // then
        verifySequence {
            eventStorage.save(ofType(GamePlayed::class))
            eventEmitter.emit(ofType(GamePlayed::class))
        }
    }
}