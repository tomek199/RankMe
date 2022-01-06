package com.tm.rankme.infrastructure.player

import com.tm.rankme.model.player.Player
import com.tm.rankme.model.player.PlayerRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class MongoPlayerRepository(
    private val accessor: MongoPlayerAccessor
) : PlayerRepository {

    override fun byId(id: String): Player? = accessor.findByIdOrNull(id)?.let {
        Player(it.id, it.leagueId, it.name, it.deviation, it.rating)
    }

    override fun store(player: Player) {
        val entity = PlayerEntity(player.id, player.leagueId, player.name, player.deviation, player.rating)
        accessor.save(entity)
    }

    override fun byLeagueId(leagueId: String): List<Player> = accessor.findAllByLeagueIdOrderByRatingDesc(leagueId).map {
        Player(it.id, it.leagueId, it.name, it.deviation, it.rating)
    }
}