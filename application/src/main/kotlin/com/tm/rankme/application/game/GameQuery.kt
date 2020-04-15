package com.tm.rankme.application.game

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import com.tm.rankme.application.common.Mapper
import com.tm.rankme.domain.game.Game
import com.tm.rankme.domain.game.GameRepository
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
class GameQuery(
    private val repository: GameRepository,
    @Qualifier("gameMapper") private val mapper: Mapper<Game, GameModel>
) : GraphQLQueryResolver {

    fun game(id: String): GameModel? {
        val game = repository.findById(id)
        return game?.let { mapper.toModel(it) }
    }
}