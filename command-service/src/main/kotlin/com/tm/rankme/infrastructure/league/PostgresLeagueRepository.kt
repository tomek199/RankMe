package com.tm.rankme.infrastructure.league

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.tm.rankme.domain.base.Event
import com.tm.rankme.domain.league.League
import com.tm.rankme.domain.league.LeagueRepository
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
class PostgresLeagueRepository(
    private val accessor: LeagueAccessor,
    private val mapper: LeagueMapper
) : LeagueRepository {

    private val log = LoggerFactory.getLogger(PostgresLeagueRepository::class.java)
    private val objectMapper: ObjectMapper = jacksonObjectMapper()

    override fun byId(id: String) = League.from(events(id))

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

    private fun events(aggregateId: String): List<Event<League>> {
        log.info("Getting events for league stream $aggregateId")
        val entities = accessor.getByAggregateIdOrderByTimestampAsc(aggregateId)
        if (entities.isEmpty()) throw InfrastructureException("Stream $aggregateId is not found")
        return entities.map { event -> mapper.deserialize(event.type, event.payload) }
    }

    override fun exist(id: String): Boolean {
        return accessor.getFirstByAggregateIdOrderByTimestampDesc(id) != null
    }
}

interface LeagueAccessor : CrudRepository<LeagueEntity, Long> {
    fun getFirstByAggregateIdOrderByTimestampDesc(aggregateId: String): LeagueEntity?
    fun getByAggregateIdOrderByTimestampAsc(aggregateId: String): List<LeagueEntity>
}

@Entity(name = "league")
data class LeagueEntity(
    val aggregateId: String,
    val type: String,
    val version: Long,
    val timestamp: Long,
    @Column(columnDefinition = "TEXT") val payload: String,
    @Id @GeneratedValue var id: Long? = null
)
