package com.tm.rankme.cqrs.command.league

import com.tm.rankme.cqrs.command.CommandHandler
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
class RenameLeagueHandler(
    private val repository: LeagueRepository,
    eventBus: EventBus
) : CommandHandler<RenameLeagueCommand>(eventBus) {

    @RabbitListener(bindings = [
        QueueBinding(
            value = Queue(name = "rename-league-command-queue"),
            exchange = Exchange(name = "rankme.api", type = "direct"),
            key = ["RenameLeagueCommand"]
        )
    ])
    override fun dispatch(command: RenameLeagueCommand) = super.dispatch(command)

    override fun execute(command: RenameLeagueCommand): List<Event<League>> {
        val league = repository.byId(command.id)
        league.rename(command.name)
        repository.store(league)
        return league.pendingEvents
    }
}