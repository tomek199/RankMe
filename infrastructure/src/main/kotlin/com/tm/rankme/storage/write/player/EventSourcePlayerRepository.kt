package com.tm.rankme.storage.write.player

import com.tm.rankme.domain.base.EventEmitter
import com.tm.rankme.domain.base.EventStorage
import com.tm.rankme.domain.player.Player
import com.tm.rankme.domain.player.PlayerRepository
import org.springframework.stereotype.Repository

@Repository
class EventSourcePlayerRepository(
    eventStorage: EventStorage<Player>,
    eventEmitter: EventEmitter
) : PlayerRepository(eventStorage, eventEmitter)