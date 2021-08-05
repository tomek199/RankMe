package com.tm.rankme.infrastructure.player

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.tm.rankme.domain.base.Event
import com.tm.rankme.domain.player.Player
import com.tm.rankme.domain.player.PlayerRepository
import com.tm.rankme.infrastructure.InfrastructureException
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Profile("postgresql")
@Repository
class PostgresPlayerRepository(
    private val accessor: PlayerAccessor,
    private val mapper: PlayerMapper
) : PlayerRepository {

    private val log = LoggerFactory.getLogger(PostgresPlayerRepository::class.java)
    private val objectMapper: ObjectMapper = jacksonObjectMapper()

    override fun byId(id: String) = Player.from(events(id))

    override fun store(aggregate: Player) = aggregate.pendingEvents.forEach {
        log.info("Saving event ${it.type} for aggregate ${it.aggregateId}")
        checkVersion(it)
        accessor.save(entity(it))
    }

    private fun checkVersion(event: Event<Player>) {
        if (event.version != 0L) {
            val entity = accessor.getFirstByAggregateIdOrderByTimestampDesc(event.aggregateId)
                ?: throw InfrastructureException("Cannon get actual version of aggregate id=${event.aggregateId}")
            if (entity.version != event.version - 1)
                throw InfrastructureException("Version mismatch of aggregate id=${event.aggregateId}")
        }
    }

    private fun entity(event: Event<Player>): PlayerEntity {
        val payload = objectMapper.writeValueAsString(mapper.serialize(event))
        return PlayerEntity(event.aggregateId, event.type, event.version, event.timestamp, payload)
    }

    private fun events(aggregateId: String): List<Event<Player>> {
        log.info("Getting events for player stream $aggregateId")
        val entities = accessor.getByAggregateIdOrderByTimestampAsc(aggregateId)
        if (entities.isEmpty()) throw InfrastructureException("Stream $aggregateId is not found")
        return entities.map { event -> mapper.deserialize(event.type, event.payload) }
    }
}

interface PlayerAccessor : CrudRepository<PlayerEntity, Long> {
    fun getFirstByAggregateIdOrderByTimestampDesc(aggregateId: String): PlayerEntity?
    fun getByAggregateIdOrderByTimestampAsc(aggregateId: String): List<PlayerEntity>
}

@Entity(name = "player")
data class PlayerEntity(
    val aggregateId: String,
    val type: String,
    val version: Long,
    val timestamp: Long,
    @Column(columnDefinition = "TEXT") val payload: String,
    @Id @GeneratedValue var id: Long? = null
)