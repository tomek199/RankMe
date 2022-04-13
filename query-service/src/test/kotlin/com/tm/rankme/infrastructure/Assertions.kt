package com.tm.rankme.infrastructure

import com.tm.rankme.infrastructure.game.GameEntity
import com.tm.rankme.model.Page
import com.tm.rankme.model.game.Game
import kotlin.test.assertEquals

fun assertGamesPage(
    expectedGames: List<GameEntity>,
    expectedHasPreviousPage: Boolean, expectedHasNextPage: Boolean,
    page: Page<Game>
) {
    assertEquals(expectedGames.size, page.items.size)
    assertEquals(expectedGames.first().id, page.items.first().node.id)
    assertEquals(expectedGames.last().id, page.items.last().node.id)
    assertEquals(expectedHasPreviousPage, page.hasPreviousPage)
    assertEquals(expectedHasNextPage, page.hasNextPage)
}