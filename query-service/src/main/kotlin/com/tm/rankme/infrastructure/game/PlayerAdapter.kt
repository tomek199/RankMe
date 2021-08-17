package com.tm.rankme.infrastructure.game

import com.tm.rankme.model.game.PlayerInfo
import com.tm.rankme.model.game.PlayerPort
import com.tm.rankme.model.player.PlayerRepository
import org.springframework.stereotype.Service

@Service
class PlayerAdapter(private val repository: PlayerRepository) : PlayerPort {
    override fun playerName(id: String): String {
        return repository.byId(id)?.name ?: throw IllegalStateException("Player $id is not found")
    }

    override fun playerInfo(id: String): PlayerInfo {
        return repository.byId(id)?.let {
            PlayerInfo(it.name, it.deviation, it.rating)
        } ?: throw IllegalStateException("Player $id is not found")
    }
}