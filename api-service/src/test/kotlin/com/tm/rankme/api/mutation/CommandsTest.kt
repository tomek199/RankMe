package com.tm.rankme.api.mutation

import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.*
import kotlin.test.assertEquals

class CommandsTest {
    private val leagueId = UUID.randomUUID()
    private val playerOneId = UUID.randomUUID()
    private val playerTwoId = UUID.randomUUID()
    private val gameId = UUID.randomUUID()

    @Test
    fun `Should init CreateLeagueCommand`() {
        // given
        val name = "Star Wars"
        // when
        val command = CreateLeagueCommand(name)
        // then
        assertEquals(name, command.name)
    }

    @Test
    internal fun `Should init RenameLeagueCommand`() {
        // given
        val newName = "Transformers"
        // when
        val command = RenameLeagueCommand(leagueId, newName)
        // then
        assertEquals(leagueId, command.id)
        assertEquals(newName, command.name)
    }

    @Test
    internal fun `Should init ChangeLeagueSettingsCommand`() {
        // given
        val allowDraws = true
        val maxScore = 7
        // when
        val command = ChangeLeagueSettingsCommand(leagueId, allowDraws, maxScore)
        // then
        assertEquals(leagueId, command.id)
        assertEquals(allowDraws, command.allowDraws)
        assertEquals(maxScore, command.maxScore)
    }

    @Test
    fun `Should init CreatePlayerCommand`() {
        // given
        val name = "Optimus Prime"
        // when
        val command = CreatePlayerCommand(leagueId, name)
        // then
        assertEquals(leagueId, command.leagueId)
        assertEquals(name, command.name)
    }

    @Test
    internal fun `Should init ScheduleGameCommand`() {
        // given
        val dateTime = LocalDateTime.now()
        // when
        val command = ScheduleGameCommand(playerOneId, playerTwoId, dateTime)
        // then
        assertEquals(playerOneId, command.playerOneId)
        assertEquals(playerTwoId, command.playerTwoId)
        assertEquals(dateTime, command.dateTime)
    }

    @Test
    internal fun `Should init CompleteGameCommand`() {
        // given
        val playerOneScore = 2
        val playerTwoScore = 3
        // when
        val command = CompleteGameCommand(gameId, playerOneScore, playerTwoScore)
        // then
        assertEquals(gameId, command.gameId)
        assertEquals(playerOneScore, command.playerOneScore)
        assertEquals(playerTwoScore, command.playerTwoScore)
    }

    @Test
    internal fun `Should init PlayGameCommand`() {
        // given
        val playerOneScore = 2
        val playerTwoScore = 3
        // when
        val command = PlayGameCommand(playerOneId, playerTwoId, playerOneScore, playerTwoScore)
        // then
        assertEquals(playerOneId, command.playerOneId)
        assertEquals(playerTwoId, command.playerTwoId)
        assertEquals(playerOneScore, command.playerOneScore)
        assertEquals(playerTwoScore, command.playerTwoScore)
    }
}