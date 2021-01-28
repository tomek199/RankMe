package com.tm.rankme.api.mutation

import com.graphql.spring.boot.test.GraphQLTestTemplate
import com.ninjasquad.springmockk.MockkBean
import com.tm.rankme.domain.base.EventBus
import com.tm.rankme.domain.game.Game
import com.tm.rankme.domain.game.GamePlayed
import com.tm.rankme.domain.game.GameRepository
import com.tm.rankme.domain.game.GameScheduled
import com.tm.rankme.domain.league.League
import com.tm.rankme.domain.league.LeagueCreated
import com.tm.rankme.domain.league.LeagueRenamed
import com.tm.rankme.domain.league.LeagueRepository
import com.tm.rankme.domain.league.LeagueSettingsChanged
import com.tm.rankme.domain.player.Player
import com.tm.rankme.domain.player.PlayerCreated
import com.tm.rankme.domain.player.PlayerPlayedGame
import com.tm.rankme.domain.player.PlayerRepository
import io.mockk.every
import io.mockk.slot
import io.mockk.verify
import io.mockk.verifyOrder
import io.mockk.verifySequence
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class MutationIntegrationTest {
    @MockkBean(relaxed = true)
    private lateinit var leagueRepository: LeagueRepository
    @MockkBean(relaxed = true)
    private lateinit var playerRepository: PlayerRepository
    @MockkBean(relaxed = true)
    private lateinit var gameRepository: GameRepository
    @MockkBean(relaxed = true)
    private lateinit var eventBus: EventBus
    @Autowired
    private lateinit var template: GraphQLTestTemplate

    @Test
    internal fun `Should execute 'create league' command`() {
        // given
        val request = "graphql/create-league.graphql"
        // when
        val response = template.postForResource(request)
        // then
        assertTrue(response.isOk)
        assertEquals(Status.SUCCESS.name, response.get("$.data.createLeague.status"))
        assertNull(response.get("$.data.createLeague.message"))
        val eventSlot = slot<LeagueCreated>()
        verify(exactly = 1) { leagueRepository.store(ofType(League::class)) }
        verify(exactly = 1) { eventBus.emit(capture(eventSlot)) }
        eventSlot.captured.let {
            assertEquals(0, it.version)
            assertEquals("Star Wars", it.name)
        }
    }

    @Test
    internal fun `Should execute 'rename name' command`() {
        // given
        val aggregateId = UUID.fromString("c4fd7f32-0a57-455d-ac64-c7cec5676723")
        every { leagueRepository.byId(aggregateId) } returns
            League.from(listOf(LeagueCreated("Star Wars", aggregateId = aggregateId)))
        val request = "graphql/rename-league.graphql"
        // when
        val response = template.postForResource(request)
        // then
        assertTrue(response.isOk)
        assertEquals(Status.SUCCESS.name, response.get("$.data.renameLeague.status"))
        assertNull(response.get("$.data.renameLeague.message"))
        val eventSlot = slot<LeagueRenamed>()
        verify(exactly = 1) { leagueRepository.store(ofType(League::class)) }
        verify(exactly = 1) { eventBus.emit(capture(eventSlot)) }
        eventSlot.captured.let {
            assertEquals(1, it.version)
            assertEquals(aggregateId, it.aggregateId)
            assertEquals("Transformers", it.name)
        }
    }

    @Test
    internal fun `Should execute 'change league settings' command`() {
        // given
        val aggregateId = UUID.fromString("0dcff3e1-5bae-4344-942c-1b2a34f97d18")
        every { leagueRepository.byId(aggregateId) } returns
            League.from(listOf(
                LeagueCreated("Star Wars", aggregateId = aggregateId),
                LeagueRenamed(aggregateId, 1, "Transformers")
            ))
        val request = "graphql/change-league-setting.graphql"
        // when
        val response = template.postForResource(request)
        // then
        assertTrue(response.isOk)
        assertEquals(Status.SUCCESS.name, response.get("$.data.changeLeagueSettings.status"))
        assertNull(response.get("$.data.changeLeagueSettings.message"))
        val eventSlot = slot<LeagueSettingsChanged>()
        verify(exactly = 1) { leagueRepository.store(ofType(League::class)) }
        verify(exactly = 1) { eventBus.emit(capture(eventSlot)) }
        eventSlot.captured.let {
            assertEquals(2, it.version)
            assertEquals(aggregateId, it.aggregateId)
            assertEquals(true, it.allowDraws)
            assertEquals(10, it.maxScore)
        }
    }

    @Test
    internal fun `Should execute 'create player' command`() {
        // given
        val leagueId = UUID.fromString("40efa486-d059-4cd2-b5e7-f4ba73b08345")
        val request = "graphql/create-player.graphql"
        every { leagueRepository.exist(leagueId) } returns true
        // when
        val response = template.postForResource(request)
        // then
        assertTrue(response.isOk)
        assertEquals(Status.SUCCESS.name, response.get("$.data.createPlayer.status"))
        assertNull(response.get("$.data.createPlayer.message"))
        val eventSlot = slot<PlayerCreated>()
        verify(exactly = 1) { playerRepository.store(ofType(Player::class)) }
        verify(exactly = 1) { eventBus.emit(capture(eventSlot)) }
        eventSlot.captured.let {
            assertEquals(0, it.version)
            assertEquals(leagueId, it.leagueId)
            assertEquals("Optimus Prime", it.name)
        }
    }

    @Test
    internal fun `Should execute 'play-game' command`() {
        // given
        val leagueId = UUID.randomUUID()
        val playerOneId = UUID.fromString("8fb3af2e-1b72-4771-9533-c4793714372a")
        val playerTwoId = UUID.fromString("3c383cad-845a-477e-a1b2-565a5080ba5f")
        every { playerRepository.byId(playerOneId) } returns Player.from(listOf(
            PlayerCreated(leagueId, "Batman", aggregateId = playerOneId)
        ))
        every { playerRepository.byId(playerTwoId) } returns Player.from(listOf(
            PlayerCreated(leagueId, "Superman", aggregateId = playerTwoId)
        ))
        val request = "graphql/play-game.graphql"
        // when
        val response = template.postForResource(request)
        // then
        assertTrue(response.isOk)
        assertEquals(Status.SUCCESS.name, response.get("$.data.playGame.status"))
        val gameSlot = slot<GamePlayed>()
        verify(exactly = 1) { gameRepository.store(ofType(Game::class)) }
        verify(exactly = 1) { eventBus.emit(gameSlot.captured) }
        gameSlot.captured.let {
            assertEquals(leagueId, it.leagueId)
            assertEquals(playerOneId, it.firstId)
            assertEquals(3, it.firstScore)
            assertEquals(-60, it.firstDeviationDelta)
            assertEquals(-162, it.firstRatingDelta)
            assertEquals(playerTwoId, it.secondId)
            assertEquals(5, it.secondScore)
            assertEquals(-60, it.secondDeviationDelta)
            assertEquals(162, it.secondRatingDelta)
            assertNotNull(it.dateTime)
        }
        verifyOrder {
            playerRepository.byId(playerOneId)
            playerRepository.byId(playerTwoId)
        }

        verifyOrder {
            playerRepository.store(ofType(Player::class))
            playerRepository.store(ofType(Player::class))
        }
        val playerOneSlot = slot<PlayerPlayedGame>()
        val playerTwoSlot = slot<PlayerPlayedGame>()
        verifyOrder {
            eventBus.emit(capture(playerOneSlot))
            eventBus.emit(capture(playerTwoSlot))
        }
        playerOneSlot.captured.let {
            assertEquals(3, it.score)
            assertEquals(-60, it.deviationDelta)
            assertEquals(-162, it.ratingDelta)
        }
        playerTwoSlot.captured.let {
            assertEquals(5, it.score)
            assertEquals(-60, it.deviationDelta)
            assertEquals(162, it.ratingDelta)
        }
    }

    @Test
    internal fun `Should execute 'schedule-game' command`() {
        // given
        val leagueId = UUID.randomUUID()
        val playerOneId = UUID.fromString("ee50ada3-d73d-46a4-aa9e-08d232dd99b8")
        val playerTwoId = UUID.fromString("2f5df9b0-2469-49f1-ac03-ac2966ff9f30")
        every { playerRepository.byId(playerOneId) } returns Player.from(listOf(
            PlayerCreated(leagueId, "Batman", aggregateId = playerOneId)
        ))
        every { playerRepository.byId(playerTwoId) } returns Player.from(listOf(
            PlayerCreated(leagueId, "Superman", aggregateId = playerTwoId)
        ))
        val request = "graphql/schedule-game.graphql"
        // when
        val response = template.postForResource(request)
        // then
        assertTrue(response.isOk)
        assertEquals(Status.SUCCESS.name, response.get("$.data.scheduleGame.status"))
        verifySequence {
            playerRepository.byId(playerOneId)
            playerRepository.byId(playerTwoId)
        }
        val gameSlot = slot<GameScheduled>()
        verify(exactly = 1) { gameRepository.store(ofType(Game::class)) }
        verify(exactly = 1) { eventBus.emit(capture(gameSlot)) }
        gameSlot.captured.let {
            assertEquals(leagueId, it.leagueId)
            assertEquals(playerOneId, it.firstId)
            assertEquals(playerTwoId, it.secondId)
            assertEquals(LocalDateTime.parse("2021-01-21T12:10:00").toEpochSecond(ZoneOffset.UTC), it.dateTime)
        }
    }

    @Test
    internal fun `Should execute 'complete-game' command`() {
        // given
        val gameId = UUID.fromString("ce75cea6-d8da-4ba6-8d54-95c0e1b44883");
        val scheduledEvent = GameScheduled(
            UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), Instant.now().toEpochMilli(), gameId
        )
        every { gameRepository.byId(gameId) } returns Game.from(listOf(scheduledEvent))
        every { playerRepository.byId(scheduledEvent.firstId) } returns Player.from(listOf(
            PlayerCreated(scheduledEvent.leagueId, "Batman", aggregateId = scheduledEvent.firstId)
        ))
        every { playerRepository.byId(scheduledEvent.secondId) } returns Player.from(listOf(
            PlayerCreated(scheduledEvent.leagueId, "Superman", aggregateId = scheduledEvent.secondId)
        ))
        val request = "graphql/complete-game.graphql"
        // when
        val response = template.postForResource(request)
        // then
        assertTrue(response.isOk)
        assertEquals(Status.SUCCESS.name, response.get("$.data.completeGame.status"))
        val gameSlot = slot<GamePlayed>()
        verify(exactly = 1) { gameRepository.store(ofType(Game::class)) }
        verify(exactly = 1) { eventBus.emit(capture(gameSlot)) }
        gameSlot.captured.let {
            assertEquals(scheduledEvent.leagueId, it.leagueId)
            assertEquals(scheduledEvent.firstId, it.firstId)
            assertEquals(1, it.firstScore)
            assertEquals(-60, it.firstDeviationDelta)
            assertEquals(162, it.firstRatingDelta)
            assertEquals(scheduledEvent.secondId, it.secondId)
            assertEquals(0, it.secondScore)
            assertEquals(-60, it.secondDeviationDelta)
            assertEquals(-162, it.secondRatingDelta)
            assertNotNull(it.dateTime)
        }
        verifyOrder {
            playerRepository.byId(scheduledEvent.firstId)
            playerRepository.byId(scheduledEvent.secondId)
        }
        verifyOrder {
            playerRepository.store(ofType(Player::class))
            playerRepository.store(ofType(Player::class))
        }
        val playerOneSlot = slot<PlayerPlayedGame>()
        val playerTwoSlot = slot<PlayerPlayedGame>()
        verifyOrder {
            eventBus.emit(playerOneSlot.captured)
            eventBus.emit(playerTwoSlot.captured)
        }
        playerOneSlot.captured.let {
            assertEquals(1, it.score)
            assertEquals(-60, it.deviationDelta)
            assertEquals(162, it.ratingDelta)
        }
        playerTwoSlot.captured.let {
            assertEquals(0, it.score)
            assertEquals(-60, it.deviationDelta)
            assertEquals(-162, it.ratingDelta)
        }
    }
}