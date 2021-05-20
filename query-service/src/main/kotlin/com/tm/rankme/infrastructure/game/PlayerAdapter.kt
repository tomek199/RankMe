package com.tm.rankme.infrastructure.game

import com.tm.rankme.model.game.PlayerInfo
import com.tm.rankme.model.game.PlayerPort
import com.tm.rankme.model.player.PlayerRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class PlayerAdapter(private val repository: PlayerRepository) : PlayerPort {
    override fun playerInfo(id: UUID): PlayerInfo {
        return repository.byId(id)?.let {
            PlayerInfo(it.name, it.deviation, it.rating)
        } ?: throw IllegalStateException("Player $id is not found")
    }
}