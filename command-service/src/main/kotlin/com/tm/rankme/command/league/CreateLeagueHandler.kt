package com.tm.rankme.command.league

import com.tm.rankme.command.CommandHandler
import com.tm.rankme.domain.base.Event
import com.tm.rankme.domain.base.EventBus
import com.tm.rankme.domain.league.League
import com.tm.rankme.domain.league.LeagueRepository
import org.springframework.amqp.rabbit.annotation.Exchange
import org.springframework.amqp.rabbit.annotation.Queue
import org.springframework.amqp.rabbit.annotation.QueueBinding
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Service

@Service
class CreateLeagueHandler(
    private val repository: LeagueRepository,
    eventBus: EventBus
) : com.tm.rankme.command.CommandHandler<CreateLeagueCommand>(eventBus) {

    @RabbitListener(bindings = [
        QueueBinding(
            value = Queue(name = "create-league-command-queue"),
            exchange = Exchange(name = "rankme.api", type = "direct"),
            key = ["CreateLeagueCommand"]
        )
    ])
    override fun dispatch(command: CreateLeagueCommand) = super.dispatch(command)

    override fun execute(command: CreateLeagueCommand): List<Event<League>> {
        val league = League.create(command.name)
        repository.store(league)
        return league.pendingEvents
    }
}