package com.tm.rankme.storage.write.league

import com.tm.rankme.domain.base.EventBus
import com.tm.rankme.domain.base.EventStorage
import com.tm.rankme.domain.league.League
import com.tm.rankme.domain.league.LeagueRepository
import com.tm.rankme.storage.write.InfrastructureException
import java.util.*
import org.springframework.stereotype.Repository

@Repository
class EventSourceLeagueRepository(
    eventStorage: EventStorage<League>,
    eventBus: EventBus
) : LeagueRepository(eventStorage, eventBus) {

    override fun exist(id: UUID): Boolean {
        return try {
            eventStorage.events(id.toString()).isNotEmpty()
        } catch (e: InfrastructureException) {
            false
        }
    }
}