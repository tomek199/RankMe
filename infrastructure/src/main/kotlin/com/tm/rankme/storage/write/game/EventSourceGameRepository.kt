package com.tm.rankme.storage.write.game

import com.tm.rankme.domain.base.EventEmitter
import com.tm.rankme.domain.base.EventStorage
import com.tm.rankme.domain.game.Game
import com.tm.rankme.domain.game.GameRepository
import org.springframework.stereotype.Repository

@Repository
class EventSourceGameRepository(
    eventStorage: EventStorage<Game>,
    eventEmitter: EventEmitter
) : GameRepository(eventStorage, eventEmitter)