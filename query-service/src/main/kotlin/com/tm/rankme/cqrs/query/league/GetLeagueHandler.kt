package com.tm.rankme.cqrs.query.league

import com.tm.rankme.cqrs.query.QueryHandler
import com.tm.rankme.model.league.League
import com.tm.rankme.model.league.LeagueRepository
import org.springframework.amqp.rabbit.annotation.Exchange
import org.springframework.amqp.rabbit.annotation.Queue
import org.springframework.amqp.rabbit.annotation.QueueBinding
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Service

@Service
class GetLeagueHandler(
    private val repository: LeagueRepository
) : QueryHandler<GetLeagueQuery, League?> {

    @RabbitListener(bindings = [
        QueueBinding(
            value = Queue(name = "get-league-query-queue"),
            exchange = Exchange(name = "rankme.api", type = "direct"),
            key = ["GetLeagueQuery"]
        )
    ])
    override fun handle(query: GetLeagueQuery): League? = repository.byId(query.id)
}