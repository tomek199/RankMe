package com.tm.rankme.api.mutation

import com.tm.rankme.cqrs.command.Command
import com.tm.rankme.cqrs.command.CommandBus
import com.tm.rankme.cqrs.command.CommandHandler
import org.springframework.context.ApplicationContext
import org.springframework.core.GenericTypeResolver
import org.springframework.stereotype.Component

@Component
class SpringCommandBus(private val applicationContext: ApplicationContext) : CommandBus {
    private val log = logger<SpringCommandBus>()
    private val handlers: Map<Class<*>, String>

    init {
        handlers = applicationContext.getBeanNamesForType(CommandHandler::class.java).map { handler ->
            val commandType = GenericTypeResolver.resolveTypeArgument(
                applicationContext.getType(handler)!!, CommandHandler::class.java
            )
            Pair<Class<*>, String>(commandType!!, handler)
        }.toMap()
    }

    override fun execute(command: Command) {
        log.info("Handle command {}", command)
        val handlerName = handlers[command::class.java] ?: throw IllegalStateException("Command type not found")
        val handler = applicationContext.getBean(handlerName) as CommandHandler<Command>
        handler.dispatch(command)
    }
}