package com.tm.rankme.infrastructure.league

import com.nhaarman.mockitokotlin2.mock

internal class EventSourceLeagueRepositoryTest {
    private val eventStorage: LeagueEventStorage = mock()
    private val repository = EventSourceLeagueRepository(eventStorage)
}