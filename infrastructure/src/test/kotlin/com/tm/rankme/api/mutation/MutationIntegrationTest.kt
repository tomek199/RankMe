package com.tm.rankme.api.mutation

import com.graphql.spring.boot.test.GraphQLTestTemplate
import com.ninjasquad.springmockk.MockkBean
import com.tm.rankme.domain.league.LeagueCreated
import com.tm.rankme.domain.league.LeagueRenamed
import com.tm.rankme.domain.league.LeagueSettingsChanged
import com.tm.rankme.storage.write.league.LeagueEventEmitter
import com.tm.rankme.storage.write.league.LeagueEventStorage
import io.mockk.every
import io.mockk.slot
import io.mockk.verify
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
    private lateinit var eventStorage: LeagueEventStorage
    @MockkBean(relaxed = true)
    private lateinit var eventEmitter: LeagueEventEmitter
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
        verify(exactly = 1) { eventStorage.save(capture(eventSlot)) }
        verify(exactly = 1) { eventEmitter.emit(eventSlot.captured) }
        assertEquals(0, eventSlot.captured.version)
        assertEquals("Star Wars", eventSlot.captured.name)
    }

    @Test
    internal fun `Should execute 'rename name' command`() {
        // given
        val aggregateId = "c4fd7f32-0a57-455d-ac64-c7cec5676723"
        every { eventStorage.events(aggregateId) } returns
            listOf(LeagueCreated("Star Wars", aggregateId = UUID.fromString(aggregateId)))
        val request = "graphql/rename-league.graphql"
        // when
        val response = template.postForResource(request)
        // then
        assertTrue(response.isOk)
        assertEquals(Status.SUCCESS.name, response.get("$.data.renameLeague.status"))
        assertNull(response.get("$.data.renameLeague.message"))
        val eventSlot = slot<LeagueRenamed>()
        verify(exactly = 1) { eventStorage.save(capture(eventSlot)) }
        verify(exactly = 1) { eventEmitter.emit(eventSlot.captured) }
        assertEquals(1, eventSlot.captured.version)
        assertEquals(aggregateId, eventSlot.captured.aggregateId.toString())
        assertEquals("Transformers", eventSlot.captured.name)
    }

    @Test
    internal fun `Should execute 'change league settings' command`() {
        // given
        val aggregateId = "0dcff3e1-5bae-4344-942c-1b2a34f97d18"
        every { eventStorage.events(aggregateId) } returns
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
        verify(exactly = 1) { eventStorage.save(capture(eventSlot)) }
        verify(exactly = 1) { eventEmitter.emit(eventSlot.captured) }
        assertEquals(2, eventSlot.captured.version)
        assertEquals(aggregateId, eventSlot.captured.aggregateId.toString())
        assertEquals(true, eventSlot.captured.allowDraws)
        assertEquals(10, eventSlot.captured.maxScore)
    }
}