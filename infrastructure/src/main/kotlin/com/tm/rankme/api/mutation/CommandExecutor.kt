package com.tm.rankme.api.mutation

import com.tm.rankme.cqrs.command.Command
import com.tm.rankme.cqrs.command.CommandBus
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class CommandExecutor(
    private val commandBus: CommandBus
) {
    private val log = LoggerFactory.getLogger(CommandExecutor::class.java)

    fun execute(command: Command): Result {
        return try {
            commandBus.execute(command)
            Result()
        } catch (exception: RuntimeException) {
            log.error(exception.message)
            Result(Status.FAILURE, exception.message)
        }
    }
}