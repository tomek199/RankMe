package com.tm.rankme.api.query.league

import com.tm.rankme.api.query.Mapper
import org.springframework.stereotype.Service

@Service
class LeagueMapper : Mapper<League>() {
    override fun deserialize(data: ByteArray): League = objectMapper.readValue(data, League::class.java)
}