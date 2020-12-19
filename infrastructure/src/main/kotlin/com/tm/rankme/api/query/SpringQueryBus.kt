package com.tm.rankme.api.query

import com.tm.rankme.cqrs.query.Query
import com.tm.rankme.cqrs.query.QueryBus
import com.tm.rankme.cqrs.query.QueryHandler
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext
import org.springframework.core.GenericTypeResolver
import org.springframework.stereotype.Component

@Component
class SpringQueryBus(private val applicationContext: ApplicationContext) : QueryBus {
    private val log = LoggerFactory.getLogger(SpringQueryBus::class.java)
    private val handlers: Map<Class<*>, String>

    init {
        handlers = applicationContext.getBeanNamesForType(QueryHandler::class.java).map { handler ->
            val types = GenericTypeResolver.resolveTypeArguments(
                applicationContext.getType(handler)!!, QueryHandler::class.java
            )
            val queryType = if (types != null && types.size == 2) types[0]
                else throw IllegalStateException("Cannot create query handlers")
            Pair<Class<*>, String>(queryType!!, handler)
        }.toMap()
    }

    override fun <R> execute(query: Query): R {
        log.info("Handle query {}", query)
        val handlerName = handlers[query::class.java] ?: throw IllegalStateException("Query type not found")
        val handler = applicationContext.getBean(handlerName) as QueryHandler<Query, R>
        return handler.handle(query)
    }
}