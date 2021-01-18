package com.tm.rankme.api.mutation

import com.graphql.spring.boot.test.GraphQLTestTemplate
import com.ninjasquad.springmockk.MockkBean
import com.tm.rankme.domain.base.EventEmitter
import com.tm.rankme.domain.game.GamePlayed
import com.tm.rankme.domain.league.LeagueCreated
import com.tm.rankme.domain.league.LeagueRenamed
import com.tm.rankme.domain.league.LeagueSettingsChanged
import com.tm.rankme.domain.player.PlayerCreated
import com.tm.rankme.domain.player.PlayerPlayedGame
import com.tm.rankme.storage.write.game.GameEventStorage
import com.tm.rankme.storage.write.league.LeagueEventStorage
import com.tm.rankme.storage.write.player.PlayerEventStorage
import io.mockk.every
import io.mockk.slot
import io.mockk.verify
import io.mockk.verifyOrder
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class MutationIntegrationTest {
    @MockkBean(relaxed = true)
    private lateinit var leagueEventStorage: LeagueEventStorage
    @MockkBean(relaxed = true)
    private lateinit var playerEventStorage: PlayerEventStorage
    @MockkBean(relaxed = true)
    private lateinit var gameEventStorage: GameEventStorage
    @MockkBean(relaxed = true)
    private lateinit var eventEmitter: EventEmitter
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
        verify(exactly = 1) { leagueEventStorage.save(capture(eventSlot)) }
        verify(exactly = 1) { eventEmitter.emit(eventSlot.captured) }
        assertEquals(0, eventSlot.captured.version)
        assertEquals("Star Wars", eventSlot.captured.name)
    }

    @Test
    internal fun `Should execute 'rename name' command`() {
        // given
        val aggregateId = "c4fd7f32-0a57-455d-ac64-c7cec5676723"
        every { leagueEventStorage.events(aggregateId) } returns
            listOf(LeagueCreated("Star Wars", aggregateId = UUID.fromString(aggregateId)))
        val request = "graphql/rename-league.graphql"
        // when
        val response = template.postForResource(request)
        // then
        assertTrue(response.isOk)
        assertEquals(Status.SUCCESS.name, response.get("$.data.renameLeague.status"))
        assertNull(response.get("$.data.renameLeague.message"))
        val eventSlot = slot<LeagueRenamed>()
        verify(exactly = 1) { leagueEventStorage.save(capture(eventSlot)) }
        verify(exactly = 1) { eventEmitter.emit(eventSlot.captured) }
        assertEquals(1, eventSlot.captured.version)
        assertEquals(aggregateId, eventSlot.captured.aggregateId.toString())
        assertEquals("Transformers", eventSlot.captured.name)
    }

    @Test
    internal fun `Should execute 'change league settings' command`() {
        // given
        val aggregateId = "0dcff3e1-5bae-4344-942c-1b2a34f97d18"
        every { leagueEventStorage.events(aggregateId) } returns
            listOf(
                LeagueCreated("Star Wars", aggregateId = UUID.fromString(aggregateId)),
                LeagueRenamed(UUID.fromString(aggregateId), 1, "Transformers")
            )
        val request = "graphql/change-league-setting.graphql"
        // when
        val response = template.postForResource(request)
        // then
        assertTrue(response.isOk)
        assertEquals(Status.SUCCESS.name, response.get("$.data.changeLeagueSettings.status"))
        assertNull(response.get("$.data.changeLeagueSettings.message"))
        val eventSlot = slot<LeagueSettingsChanged>()
        verify(exactly = 1) { leagueEventStorage.save(capture(eventSlot)) }
        verify(exactly = 1) { eventEmitter.emit(eventSlot.captured) }
        assertEquals(2, eventSlot.captured.version)
        assertEquals(aggregateId, eventSlot.captured.aggregateId.toString())
        assertEquals(true, eventSlot.captured.allowDraws)
        assertEquals(10, eventSlot.captured.maxScore)
    }

    @Test
    internal fun `Should execute 'create player' command`() {
        // given
        val leagueId = "40efa486-d059-4cd2-b5e7-f4ba73b08345"
        val request = "graphql/create-player.graphql"
        every { leagueEventStorage.events(leagueId) } returns listOf(LeagueCreated("Transformers"))
        // when
        val response = template.postForResource(request)
        // then
        assertTrue(response.isOk)
        assertEquals(Status.SUCCESS.name, response.get("$.data.createPlayer.status"))
        assertNull(response.get("$.data.createPlayer.message"))
        val eventSlot = slot<PlayerCreated>()
        verify(exactly = 1) { playerEventStorage.save(capture(eventSlot)) }
        verify(exactly = 1) { eventEmitter.emit(eventSlot.captured) }
        assertEquals(0, eventSlot.captured.version)
        assertEquals(leagueId, eventSlot.captured.leagueId.toString())
        assertEquals("Optimus Prime", eventSlot.captured.name)
    }

    @Test
    internal fun `Should execute 'play-game' command`() {
        // given
        val leagueId = UUID.randomUUID()
        val playerOneId = "8fb3af2e-1b72-4771-9533-c4793714372a"
        val playerTwoId = "3c383cad-845a-477e-a1b2-565a5080ba5f"
        every { playerEventStorage.events(playerOneId) } returns listOf(
            PlayerCreated(leagueId, "Batman", aggregateId = UUID.fromString(playerOneId))
        )
        every { playerEventStorage.events(playerTwoId) } returns listOf(
            PlayerCreated(leagueId, "Superman", aggregateId = UUID.fromString(playerTwoId))
        )
        val request = "graphql/play-game.graphql"
        // when
        val response = template.postForResource(request)
        // then
        assertTrue(response.isOk)
        assertEquals(Status.SUCCESS.name, response.get("$.data.playGame.status"))
        val gameSlot = slot<GamePlayed>()
        verify(exactly = 1) { gameEventStorage.save(capture(gameSlot))}
        verify(exactly = 1) { eventEmitter.emit(gameSlot.captured)}
        gameSlot.captured.let {
            assertEquals(leagueId, it.leagueId)
            assertEquals(UUID.fromString(playerOneId), it.firstId)
            assertEquals(3, it.firstScore)
            assertEquals(-60, it.firstDeviationDelta)
            assertEquals(-162, it.firstRatingDelta)
            assertEquals(UUID.fromString(playerTwoId), it.secondId)
            assertEquals(5, it.secondScore)
            assertEquals(-60, it.secondDeviationDelta)
            assertEquals(162, it.secondRatingDelta)
        }
        verifyOrder {
            playerEventStorage.events(playerOneId)
            playerEventStorage.events(playerTwoId)
        }
        val playerOneSlot = slot<PlayerPlayedGame>()
        val playerTwoSlot = slot<PlayerPlayedGame>()
        verifyOrder {
            playerEventStorage.save(capture(playerOneSlot))
            playerEventStorage.save(capture(playerTwoSlot))
        }
        verifyOrder {
            eventEmitter.emit(playerOneSlot.captured)
            eventEmitter.emit(playerTwoSlot.captured)
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
}