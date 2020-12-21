package com.tm.rankme.storage.write.league

import com.tm.rankme.domain.base.EventEmitter
import com.tm.rankme.domain.base.EventStorage
import com.tm.rankme.domain.league.League
import com.tm.rankme.domain.league.LeagueRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository

@Repository
class EventSourceLeagueRepository @Autowired constructor(
    eventStorage: EventStorage<League>,
    eventEmitter: EventEmitter
) : LeagueRepository(eventStorage, eventEmitter)