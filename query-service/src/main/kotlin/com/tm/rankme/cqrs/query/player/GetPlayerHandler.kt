package com.tm.rankme.cqrs.query.player

import com.tm.rankme.cqrs.query.QueryHandler
import com.tm.rankme.model.player.Player
import com.tm.rankme.model.player.PlayerRepository
import org.springframework.amqp.rabbit.annotation.Exchange
import org.springframework.amqp.rabbit.annotation.Queue
import org.springframework.amqp.rabbit.annotation.QueueBinding
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Service

@Service
class GetPlayerHandler(
    private val repository: PlayerRepository
) : QueryHandler<GetPlayerQuery, Player?> {

    @RabbitListener(bindings = [
        QueueBinding(
            value = Queue(name = "get-player-query-queue"),
            exchange = Exchange(name = "rankme.api", type = "direct"),
            key = ["GetPlayerQuery"]
        )
    ])
    override fun handle(query: GetPlayerQuery): Player? = repository.byId(query.id)
}