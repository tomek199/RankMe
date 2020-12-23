package com.tm.rankme.cqrs.query.player

import com.tm.rankme.cqrs.query.QueryHandler
import com.tm.rankme.model.player.Player
import com.tm.rankme.model.player.PlayerRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class GetPlayerHandler @Autowired constructor(
    private val repository: PlayerRepository
) : QueryHandler<GetPlayerQuery, Player?> {

    override fun handle(query: GetPlayerQuery): Player? = repository.byId(query.id)
}