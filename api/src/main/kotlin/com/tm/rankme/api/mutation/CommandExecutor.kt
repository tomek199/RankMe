package com.tm.rankme.api.mutation

import com.tm.rankme.application.cqrs.Command
import com.tm.rankme.application.cqrs.CommandBus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class CommandExecutor @Autowired constructor(
    private val commandBus: CommandBus
) {
    fun execute(command: Command): Result {
        try {
            commandBus.execute(command)
        } catch (exception: RuntimeException) {
            return Result(Status.FAILURE, exception.message)
        }
        return Result()
    }
}