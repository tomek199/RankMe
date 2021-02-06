package com.tm.rankme.api.query.player

import com.tm.rankme.api.query.Mapper
import org.springframework.stereotype.Service

@Service
class PlayerMapper : Mapper<Player>() {
    override fun deserialize(data: ByteArray): Player = objectMapper.readValue(data, Player::class.java)
}