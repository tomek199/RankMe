package com.tm.rankme.infrastructure.game

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.tm.rankme.domain.base.Event
import com.tm.rankme.domain.game.Game
import com.tm.rankme.domain.game.GameRepository
import com.tm.rankme.infrastructure.InfrastructureException
import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Primary
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Primary // todo provide profile
@Repository
class PostgresGameRepository(
    private val accessor: GameAccessor,
    private val mapper: GameMapper
) : GameRepository {

    private val log = LoggerFactory.getLogger(PostgresGameRepository::class.java)
    private val objectMapper: ObjectMapper = jacksonObjectMapper()

    override fun byId(id: UUID) = Game.from(events(id))

    override fun store(aggregate: Game) = aggregate.pendingEvents.forEach {
        log.info("Saving event ${it.type} for aggregate ${it.aggregateId}")
        checkVersion(it)
        accessor.save(entity(it))
    }

    private fun checkVersion(event: Event<Game>) {
        if (event.version != 0L) {
            val entity = accessor.getFirstByAggregateIdOrderByTimestampDesc(event.aggregateId)
                ?: throw InfrastructureException("Cannon get actual version of aggregate id=${event.aggregateId}")
            if (entity.version != event.version - 1)
                throw InfrastructureException("Version mismatch of aggregate id=${event.aggregateId}")
        }
    }

    private fun entity(event: Event<Game>): GameEntity {
        val payload = objectMapper.writeValueAsString(mapper.serialize(event))
        return GameEntity(event.aggregateId, event.type, event.version, event.timestamp, payload)
    }

    private fun events(aggregateId: UUID): List<Event<Game>> {
        log.info("Getting events for game stream $aggregateId")
        val entities = accessor.getByAggregateIdOrderByTimestampAsc(aggregateId)
        if (entities.isEmpty()) throw InfrastructureException("Stream $aggregateId is not found")
        return entities.map { event -> mapper.deserialize(event.type, event.payload) }
    }
}

interface GameAccessor : CrudRepository<GameEntity, Long> {
    fun getFirstByAggregateIdOrderByTimestampDesc(aggregateId: UUID): GameEntity?
    fun getByAggregateIdOrderByTimestampAsc(aggregateId: UUID): List<GameEntity>
}

@Entity(name = "game")
data class GameEntity(
    val aggregateId: UUID,
    val type: String,
    val version: Long,
    val timestamp: Long,
    val payload: String,
    @Id @GeneratedValue var id: Long? = null
)