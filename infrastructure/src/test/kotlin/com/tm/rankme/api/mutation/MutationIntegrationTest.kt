package com.tm.rankme.api.mutation

import com.graphql.spring.boot.test.GraphQLTestTemplate
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.verify
import com.tm.rankme.domain.league.LeagueCreated
import com.tm.rankme.domain.league.LeagueRenamed
import com.tm.rankme.domain.league.LeagueSettingsChanged
import com.tm.rankme.storage.write.league.LeagueEventEmitter
import com.tm.rankme.storage.write.league.LeagueEventStorage
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class MutationIntegrationTest {
    @MockBean
    private lateinit var eventStorage: LeagueEventStorage
    @MockBean
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
        val eventCaptor = argumentCaptor<LeagueCreated>()
        verify(eventStorage).save(eventCaptor.capture())
        verify(eventEmitter).emit(eventCaptor.firstValue)
        assertEquals(0, eventCaptor.firstValue.version)
        assertEquals("Star Wars", eventCaptor.firstValue.name)
    }

    @Test
    internal fun `Should execute 'rename name' command`() {
        // given
        val aggregateId = "c4fd7f32-0a57-455d-ac64-c7cec5676723"
        given(eventStorage.events(aggregateId))
            .willReturn(listOf(LeagueCreated("Star Wars", aggregateId = UUID.fromString(aggregateId))))
        val request = "graphql/rename-league.graphql"
        // when
        val response = template.postForResource(request)
        // then
        assertTrue(response.isOk)
        assertEquals(Status.SUCCESS.name, response.get("$.data.renameLeague.status"))
        assertNull(response.get("$.data.renameLeague.message"))
        val eventCaptor = argumentCaptor<LeagueRenamed>()
        verify(eventStorage).save(eventCaptor.capture())
        verify(eventEmitter).emit(eventCaptor.firstValue)
        assertEquals(1, eventCaptor.firstValue.version)
        assertEquals(aggregateId, eventCaptor.firstValue.aggregateId.toString())
        assertEquals("Transformers", eventCaptor.firstValue.name)
    }

    @Test
    internal fun `Should execute 'change league settings' command`() {
        // given
        val aggregateId = "0dcff3e1-5bae-4344-942c-1b2a34f97d18"
        given(eventStorage.events(aggregateId))
            .willReturn(listOf(
                LeagueCreated("Star Wars", aggregateId = UUID.fromString(aggregateId)),
                LeagueRenamed(UUID.fromString(aggregateId), 1, "Transformers")
            ))
        val request = "graphql/change-league-setting.graphql"
        // when
        val response = template.postForResource(request)
        // then
        assertTrue(response.isOk)
        assertEquals(Status.SUCCESS.name, response.get("$.data.changeLeagueSettings.status"))
        assertNull(response.get("$.data.changeLeagueSettings.message"))
        val eventCaptor = argumentCaptor<LeagueSettingsChanged>()
        verify(eventStorage).save(eventCaptor.capture())
        verify(eventEmitter).emit(eventCaptor.firstValue)
        assertEquals(2, eventCaptor.firstValue.version)
        assertEquals(aggregateId, eventCaptor.firstValue.aggregateId.toString())
        assertEquals(true, eventCaptor.firstValue.allowDraws)
        assertEquals(10, eventCaptor.firstValue.maxScore)
    }
}