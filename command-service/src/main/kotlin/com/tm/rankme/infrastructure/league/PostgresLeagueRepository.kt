package com.tm.rankme.infrastructure.league

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.tm.rankme.domain.base.Event
import com.tm.rankme.domain.league.League
import com.tm.rankme.domain.league.LeagueRepository
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
class PostgresLeagueRepository(
    private val accessor: LeagueAccessor,
    private val mapper: LeagueMapper
) : LeagueRepository {

    private val log = LoggerFactory.getLogger(PostgresLeagueRepository::class.java)
    private val objectMapper: ObjectMapper = jacksonObjectMapper()

    override fun byId(id: UUID) = League.from(events(id))

    override fun store(aggregate: League) = aggregate.pendingEvents.forEach {
        log.info("Saving event ${it.type} for aggregate ${it.aggregateId}")
        checkVersion(it)
        accessor.save(entity(it))
    }

    private fun checkVersion(event: Event<League>) {
        if (event.version != 0L) {
            val entity = accessor.getFirstByAggregateIdOrderByTimestampDesc(event.aggregateId)
                ?: throw InfrastructureException("Cannon get actual version of aggregate id=${event.aggregateId}")
            if (entity.version != event.version - 1)
                throw InfrastructureException("Version mismatch of aggregate id=${event.aggregateId}")
        }
    }

    private fun entity(event: Event<League>): LeagueEntity {
        val payload = objectMapper.writeValueAsString(mapper.serialize(event))
        return LeagueEntity(event.aggregateId, event.type, event.version, event.timestamp, payload)
    }

    private fun events(aggregateId: UUID): List<Event<League>> {
        log.info("Getting events for stream $aggregateId")
        val entities = accessor.getByAggregateIdOrderByTimestampAsc(aggregateId)
        if (entities.isEmpty()) throw InfrastructureException("Stream $aggregateId is not found")
        return entities.map { event -> mapper.deserialize(event.type, event.payload) }
    }

    override fun exist(id: UUID): Boolean {
        return accessor.getFirstByAggregateIdOrderByTimestampDesc(id) != null
    }
}

interface LeagueAccessor : CrudRepository<LeagueEntity, Long> {
    fun getFirstByAggregateIdOrderByTimestampDesc(aggregateId: UUID): LeagueEntity?
    fun getByAggregateIdOrderByTimestampAsc(aggregateId: UUID): List<LeagueEntity>
}

@Entity(name = "league")
data class LeagueEntity(
    val aggregateId: UUID,
    val type: String,
    val version: Long,
    val timestamp: Long,
    val payload: String,
    @Id @GeneratedValue var id: Long? = null
)
