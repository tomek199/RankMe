package com.tm.rankme.storage.read.player

import com.tm.rankme.model.player.Player
import com.tm.rankme.model.player.PlayerRepository
import java.util.*
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class MongoPlayerRepository(
    private val playerAccessor: MongoPlayerAccessor
) : PlayerRepository {

    override fun byId(id: UUID): Player? = playerAccessor.findByIdOrNull(id)?.let {
        Player(it.id, it.name, it.deviation, it.rating)
    }
}