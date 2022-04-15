package com.tm.rankme.infrastructure.league

import com.tm.rankme.infrastructure.decode
import com.tm.rankme.infrastructure.encode
import com.tm.rankme.model.Item
import com.tm.rankme.model.Page
import com.tm.rankme.model.league.League
import com.tm.rankme.model.league.LeagueRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class MongoLeagueRepository(
    private val accessor: MongoLeagueAccessor
) : LeagueRepository {

    override fun byId(id: String): League? = accessor.findByIdOrNull(id)?.let {
        League(it.id, it.name, it.allowDraws, it.maxScore)
    }

    override fun store(league: League) {
        val entity = LeagueEntity(league.id, league.name, league.allowDraws, league.maxScore)
        accessor.save(entity)
    }

    override fun list(first: Int): Page<League> {
        val pageable = PageRequest.of(0, first)
        val page = accessor.getAllByOrderByTimestampAsc(pageable)
        val leagues: List<Item<League>> = page.content.map(this::itemForEntity)
        return Page(leagues, false, page.hasNext())
    }

    override fun listAfter(first: Int, after: String): Page<League> {
        val pageable = PageRequest.of(0, first)
        val page = accessor.getByTimestampGreaterThanOrderByTimestampAsc(decode(after), pageable)
        val leagues: List<Item<League>> = page.content.map(this::itemForEntity)
        return Page(leagues, true, page.hasNext())
    }

    override fun listBefore(first: Int, before: String): Page<League> {
        val pageable = PageRequest.of(0, first)
        val page = accessor.getByTimestampLessThanOrderByTimestampDesc(decode(before), pageable)
        val leagues: List<Item<League>> = page.content.reversed().map(this::itemForEntity)
        return Page(leagues, page.hasNext(), true)
    }

    private fun itemForEntity(entity: LeagueEntity) = Item(leagueFromEntity(entity), encode(entity.timestamp))

    private fun leagueFromEntity(entity: LeagueEntity): League = League(
        entity.id, entity.name, entity.allowDraws, entity.maxScore
    )
}