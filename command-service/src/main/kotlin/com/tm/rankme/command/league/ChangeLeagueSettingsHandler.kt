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
class ChangeLeagueSettingsHandler(
    private val repository: LeagueRepository,
    eventBus: EventBus
) : com.tm.rankme.command.CommandHandler<ChangeLeagueSettingsCommand>(eventBus) {

    @RabbitListener(bindings = [
        QueueBinding(
            value = Queue(name = "change-league-settings-command-queue"),
            exchange = Exchange(name = "rankme.api", type = "direct"),
            key = ["ChangeLeagueSettingsCommand"]
        )
    ])
    override fun dispatch(command: ChangeLeagueSettingsCommand) = super.dispatch(command)

    override fun execute(command: ChangeLeagueSettingsCommand): List<Event<League>> {
        val league = repository.byId(command.id)
        league.settings(command.allowDraws, command.maxScore)
        repository.store(league)
        return league.pendingEvents
    }
}